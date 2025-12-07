package com.example.bloom.screen

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.bloom.R
import com.example.bloom.viewModel.DiscoveryViewModel
import com.example.bloom.viewModel.CaptureState
import com.example.bloom.util.AppViewModelFactory
import com.example.bloom.AUTH_REPO
import com.example.bloom.DISCOVERY_REPO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryCaptureAndAnalyzer(navController: NavHostController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //1. INJECTION DU VIEWMODEL avec Factory (Nouveau)
    val factory = remember {
        val application = context.applicationContext as Application
        AppViewModelFactory(application, AUTH_REPO, DISCOVERY_REPO)
    }
    val viewModel: DiscoveryViewModel = viewModel(factory = factory)

    //État du ViewModel
    val captureState by viewModel.captureState.collectAsState()

    //États locaux pour l'UI
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // 2. LOGIQUE UI BASÉE SUR L'ÉTAT DU VIEWMODEL (Nouveau)
    LaunchedEffect(captureState) {
        when (val state = captureState) {
            is CaptureState.AIProcessing -> {
                loading = true
                errorMsg = null
            }
            is CaptureState.Saving -> {
            }
            is CaptureState.Success -> {
                loading = false
                Toast.makeText(context, "Découverte enregistrée!", Toast.LENGTH_SHORT).show()
                // Redirection vers l'écran Journal
                navController.navigate("journal") {
                    popUpTo("capture") { inclusive = true }
                }
                viewModel.resetCaptureState()
            }
            is CaptureState.Error -> {
                loading = false
                errorMsg = state.message
                Toast.makeText(context, "Erreur: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetCaptureState()
            }
            else -> {
                loading = false
            }
        }
    }

    // Launcher pour galerie
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            try {
                val stream = context.contentResolver.openInputStream(it)
                bitmap = BitmapFactory.decodeStream(stream)
            } catch (e: Exception) {
                errorMsg = "Impossible de charger l'image."
            }
        }
    }

    // Launcher pour caméra
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bmp?.let {
            bitmap = it

            //Un fichier temporaire pour générer un Uri temporaire
            val tempFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            selectedUri = Uri.fromFile(tempFile)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Capture Discovery") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.popBackStack("journal", false) }
            ) { Text("List") }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Affichage de l'image
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )
            } ?: Box(
                Modifier.fillMaxWidth().height(300.dp).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) { Text("No image selected") }

            if (loading) {
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator()
                // Afficher l'état en cours
                Text(
                    when(captureState) {
                        is CaptureState.AIProcessing -> "Analyzing image..."
                        is CaptureState.Saving -> "Saving and synchronizing..."
                        else -> "Processing..."
                    }
                )
            }

            errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            // Boutons Capture / Galerie (Design conservé)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Bouton Capture Photo
                Button(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Capture Photo", fontSize = 14.sp)
                }

                //Bouton Select from Gallery
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera), // R.drawable.camera doit exister
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("From Gallery", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            //Bouton Analyse & Upload
            Button(
                enabled = bitmap != null && selectedUri != null && !loading,
                onClick = {
                    loading = true
                    errorMsg = null
                    val currentBitmap = bitmap!!
                    val currentUri = selectedUri!!

                    coroutineScope.launch(Dispatchers.IO) {

                        // 1. Sauvegarde l'image localement pour obtenir le chemin absolu
                        val fileName = "discovery_${UUID.randomUUID()}.jpg"
                        val file = File(context.filesDir, fileName)
                        FileOutputStream(file).use { out ->
                            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                        }

                        //2. APPEL UNIQUE AU VIEWMODEL (Nouveau)
                        viewModel.processAndSaveDiscovery(
                            bitmap = currentBitmap,
                            imageUri = currentUri,
                            localImagePath = file.absolutePath
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (loading) "Processing..." else "Detect & Save (Gemini AI)")
            }
        }
    }
}