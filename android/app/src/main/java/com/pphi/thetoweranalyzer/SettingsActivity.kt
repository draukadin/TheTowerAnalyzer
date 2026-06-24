package com.pphi.thetoweranalyzer

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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

        prefs = Prefs(this)

        val endpointAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Endpoint.ALL)
        endpointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRegion.adapter = endpointAdapter

        loadPrefs()

        binding.radioGroupMode.setOnCheckedChangeListener { _, _ -> updateSectionVisibility() }
        binding.btnSave.setOnClickListener { savePrefs() }
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
