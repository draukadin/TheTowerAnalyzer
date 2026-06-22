package com.pphi.thetoweranalyzer

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pphi.thetoweranalyzer.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        prefs = Prefs(this)

        // Position 0 is a non-selectable prompt; run types follow it.
        val runTypeItems = listOf(getString(R.string.prompt_run_type)) + RunType.ALL.map { it.displayName }
        val runTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, runTypeItems)
        runTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRunType.adapter = runTypeAdapter

        val dissonanceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, DissonanceType.ALL)
        dissonanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDissonanceType.adapter = dissonanceAdapter

        binding.spinnerRunType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selected = selectedRunType()
                binding.rowDissonanceType.visibility =
                    if (selected?.requiresDissonanceType == true) View.VISIBLE else View.GONE
                updateSubmitEnabled()
            }
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }

        binding.btnSubmit.setOnClickListener { onSubmit() }
    }

    override fun onResume() {
        super.onResume()
        val modeName = when (prefs.mode) {
            SubmitMode.CENTRALIZED -> "Centralized"
            SubmitMode.LEGACY -> "Legacy"
        }
        binding.tvMode.text = "Mode: $modeName"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSubmit() {
        val report = clipboardText()
        if (report.isNullOrBlank()) {
            showToast("Clipboard is empty — copy a battle report first")
            return
        }

        val runType = selectedRunType() ?: return
        val dissonanceType = if (runType.requiresDissonanceType)
            binding.spinnerDissonanceType.selectedItem as DissonanceType
        else null

        when (prefs.mode) {
            SubmitMode.CENTRALIZED -> submitCentralized(runType, dissonanceType, report)
            SubmitMode.LEGACY -> submitLegacy(runType, dissonanceType, report)
        }
    }

    private fun submitCentralized(runType: RunType, dissonanceType: DissonanceType?, report: String) {
        val endpoint = prefs.centralizedEndpoint
        val playerId = prefs.playerId

        if (playerId.isBlank()) {
            showToast("Set your Player ID in Settings before submitting")
            return
        }

        setSubmitting(true)
        lifecycleScope.launch {
            val result = ApiClient.submitCentralized(endpoint, playerId, runType, dissonanceType, report)
            setSubmitting(false)
            handleResult(result)
        }
    }

    private fun submitLegacy(runType: RunType, dissonanceType: DissonanceType?, report: String) {
        val webhookUrl = prefs.legacyWebhookUrl

        if (webhookUrl.isBlank()) {
            showToast("Configure Settings before submitting")
            return
        }

        setSubmitting(true)
        lifecycleScope.launch {
            val result = ApiClient.submitLegacy(webhookUrl, runType, dissonanceType, report)
            setSubmitting(false)
            handleResult(result)
        }
    }

    private fun handleResult(result: SubmitResult) {
        when (result) {
            is SubmitResult.Success -> {
                showToast("Report submitted successfully")
                // Reset to the run-type prompt; this re-disables Submit via the listener.
                binding.spinnerRunType.setSelection(0)
            }
            is SubmitResult.Failure -> showToast("Error ${result.statusCode}: ${result.message}")
            is SubmitResult.NetworkError -> showToast("Network error: ${result.message}")
        }
    }

    /** Selected run type, or null while the prompt (position 0) is showing. */
    private fun selectedRunType(): RunType? {
        val pos = binding.spinnerRunType.selectedItemPosition
        return if (pos <= 0) null else RunType.ALL[pos - 1]
    }

    private fun updateSubmitEnabled() {
        binding.btnSubmit.isEnabled = selectedRunType() != null
    }

    private fun setSubmitting(submitting: Boolean) {
        if (submitting) {
            binding.btnSubmit.isEnabled = false
            binding.btnSubmit.text = "Submitting…"
        } else {
            binding.btnSubmit.text = getString(R.string.btn_submit)
            updateSubmitEnabled()
        }
    }

    private fun clipboardText(): String? {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return cm.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
