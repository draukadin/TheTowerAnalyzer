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

        val runTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, RunType.ALL)
        runTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRunType.adapter = runTypeAdapter

        val dissonanceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, DissonanceType.ALL)
        dissonanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDissonanceType.adapter = dissonanceAdapter

        binding.spinnerRunType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selected = RunType.ALL[pos]
                binding.rowDissonanceType.visibility =
                    if (selected.requiresDissonanceType) View.VISIBLE else View.GONE
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

        val runType = binding.spinnerRunType.selectedItem as RunType
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

        if (endpoint.isBlank() || playerId.isBlank()) {
            showToast("Configure Settings before submitting")
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
            is SubmitResult.Success -> showToast("Report submitted successfully")
            is SubmitResult.Failure -> showToast("Error ${result.statusCode}: ${result.message}")
            is SubmitResult.NetworkError -> showToast("Network error: ${result.message}")
        }
    }

    private fun setSubmitting(submitting: Boolean) {
        binding.btnSubmit.isEnabled = !submitting
        binding.btnSubmit.text = if (submitting) "Submitting…" else getString(R.string.btn_submit)
    }

    private fun clipboardText(): String? {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return cm.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
