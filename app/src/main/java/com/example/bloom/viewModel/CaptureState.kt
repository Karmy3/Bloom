package com.example.bloom.viewModel

import com.example.bloom.model.model.entities.Discovery

sealed class CaptureState {


    data object Idle : CaptureState()
    data class AIProcessing(val localPath: String) : CaptureState()
    data class Saving(val entry: Discovery) : CaptureState()
    data class Success(val discoveryId: String) : CaptureState()
    data class Error(val message: String) : CaptureState()
}
