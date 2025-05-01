package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity4 : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var playVideoButton: Button
    private lateinit var recordVideoButton: Button

    private lateinit var playAudioButton: Button
    private lateinit var pauseAudioButton: Button
    private lateinit var stopAudioButton: Button
    private lateinit var recordAudioButton: Button
    private lateinit var imageView: ImageView

    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isAudioRecording = false
    private var currentAudioPath: String? = null
    private var videoUri: Uri? = null

    private val VIDEO_CAPTURE_REQUEST = 101
    private val CAMERA_PERMISSION_REQUEST = 303
    private val RECORD_AUDIO_PERMISSION_REQUEST = 404

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        // Video view and buttons
        videoView = findViewById(R.id.videoView)
        playVideoButton = findViewById(R.id.btnPlayVideo)
        recordVideoButton = findViewById(R.id.btnRecordVideo)

        // Setup video controls
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        recordVideoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST
                )
            } else {
                startVideoRecording()
            }
        }
        playVideoButton.setOnClickListener {
            if (videoUri != null) {
                videoView.setVideoURI(videoUri)
                videoView.start()
            } else {
                Toast.makeText(this, "No video recorded yet", Toast.LENGTH_SHORT).show()
            }
        }

        // Audio controls and image view
        playAudioButton = findViewById(R.id.btnPlayAudio)
        pauseAudioButton = findViewById(R.id.btnPauseAudio)
        stopAudioButton = findViewById(R.id.btnStopAudio)
        recordAudioButton = findViewById(R.id.btnRecordAudio)
        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.ic_launcher_background)

        // Initially, you might want to preload an audio resource if needed.
        // But if you want to play the recorded audio, you'll initialize mediaPlayer when playing.
        playAudioButton.setOnClickListener {
            playRecordedAudio()
        }
        pauseAudioButton.setOnClickListener {
            mediaPlayer?.pause()
        }
        stopAudioButton.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    it.seekTo(0)
                }
            }
        }
        recordAudioButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_PERMISSION_REQUEST
                )
            } else {
                toggleAudioRecording()
            }
        }
    }

    private fun startVideoRecording() {
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (videoIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(videoIntent, VIDEO_CAPTURE_REQUEST)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleAudioRecording() {
        if (isAudioRecording) {
            // Stop recording audio
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                isAudioRecording = false
                Toast.makeText(this, "Audio recording stopped", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error stopping audio recording", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Start recording audio
            val audioFile = createAudioFile()
            currentAudioPath = audioFile.absolutePath

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(currentAudioPath)
                try {
                    prepare()
                    start()
                    isAudioRecording = true
                    Toast.makeText(this@MainActivity4, "Audio recording started", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity4, "Audio recording failed to start", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createAudioFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "AUDIO_$timestamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        return File.createTempFile(fileName, ".3gp", storageDir)
    }

    private fun playRecordedAudio() {
        if (currentAudioPath == null) {
            Toast.makeText(this, "No recorded audio available", Toast.LENGTH_SHORT).show()
            return
        }

        val audioFile = File(currentAudioPath!!)
        if (!audioFile.exists()) {
            Toast.makeText(this, "Recorded audio file not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Release any previous mediaPlayer
        mediaPlayer?.release()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(currentAudioPath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error playing recorded audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startVideoRecording()
                }
            }
            RECORD_AUDIO_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toggleAudioRecording()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == VIDEO_CAPTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            videoUri = data?.data
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}