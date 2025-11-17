package ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.smartcallassistant.databinding.ActivityMainBinding
import ui.viewmodel.CallViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                1
            )
            return
        }


        binding.btnFetch.setOnClickListener {
            viewModel.loadCallLogs()
        }

        binding.btnAnalyze.setOnClickListener {
            viewModel.analyzeCalls()
        }

        viewModel.summary.observe(this) {
            binding.tvSummary.text = it
        }
    }
}