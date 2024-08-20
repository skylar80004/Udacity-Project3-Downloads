package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val downloadName = intent.getStringExtra(MainActivity.DOWNLOAD_NAME)
        val downloadStatus = intent.getStringExtra(MainActivity.DOWNLOAD_STATUS)

        binding.contentDetail.tvStatusValue.text = downloadStatus
        binding.contentDetail.tvFileNameValue.text = downloadName

        if (downloadStatus == DownloadStatus.SUCCESS.value) {
            binding.contentDetail.tvStatusValue.setTextAppearance(R.style.DetailValueSuccess)
        } else {
            binding.contentDetail.tvStatusValue.setTextAppearance(R.style.DetailValueSuccess)
        }
        binding.contentDetail.btnOk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
