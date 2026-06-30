package com.pphi.thetoweranalyzer

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.pphi.thetoweranalyzer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        applyContentInsets()

        prefs = Prefs(this)

        val endpointAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Endpoint.ALL)
        endpointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRegion.adapter = endpointAdapter

        loadPrefs()

        binding.radioGroupMode.setOnCheckedChangeListener { _, _ -> updateSectionVisibility() }
        binding.btnSave.setOnClickListener { savePrefs() }
    }

    /** Pad the scroll content above the gesture/navigation bar in edge-to-edge mode. */
    private fun applyContentInsets() {
        val basePadding = binding.content.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.content) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = basePadding + bars.bottom)
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadPrefs() {
        binding.etPlayerId.setText(prefs.playerId)
        binding.spinnerRegion.setSelection(Endpoint.ALL.indexOf(prefs.endpoint))
        binding.etLegacyWebhookUrl.setText(prefs.legacyWebhookUrl)

        when (prefs.mode) {
            SubmitMode.CENTRALIZED -> binding.radioCentralized.isChecked = true
            SubmitMode.LEGACY -> binding.radioLegacy.isChecked = true
        }
        updateSectionVisibility()
    }

    private fun savePrefs() {
        prefs.playerId = binding.etPlayerId.text.toString().trim()
        prefs.endpoint = binding.spinnerRegion.selectedItem as Endpoint
        prefs.legacyWebhookUrl = binding.etLegacyWebhookUrl.text.toString().trim()
        prefs.mode = if (binding.radioCentralized.isChecked) SubmitMode.CENTRALIZED else SubmitMode.LEGACY
        finish()
    }

    private fun updateSectionVisibility() {
        val centralized = binding.radioCentralized.isChecked
        binding.sectionCentralized.visibility = if (centralized) View.VISIBLE else View.GONE
        binding.sectionLegacy.visibility = if (centralized) View.GONE else View.VISIBLE
    }
}
