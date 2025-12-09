import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bloom.R
import com.example.bloom.model.model.entities.Discovery
import com.example.bloom.ui.theme.Shapes
import com.example.bloom.viewModel.DiscoveryDetailViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun shareDiscoveryIntent(context: Context, entry: Discovery, fileUri: Uri? = null) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        val shareText = "I discovered: ${entry.name}! \n${entry.fact}"
        putExtra(Intent.EXTRA_TEXT, shareText)
        if (fileUri != null) {
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = context.contentResolver.getType(fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
        }
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share the discovery"))
}

fun generateDiscoveryFile(context: Context, entry: Discovery, fileFormat: String): Uri? {
    val fileName = "discovery_${entry.id}_${entry.name.replace(" ", "_").take(10)}.$fileFormat"
    val file = File(context.cacheDir, fileName)
    val fileContent = """
        --- Découverte de Plante IA ---
        Nom Scientifique/Populaire: ${entry.name}
        Fait Intéressant (IA): ${entry.fact}
        Découvert le: ${SimpleDateFormat("MMMM dd, yyyy à HH:mm", Locale.getDefault()).format(Date(entry.timestamp))}
        ID Découverte: ${entry.id}
        ------------------------------
    """.trimIndent()
    try {
        file.writeText(fileContent)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryDetailScreen(navController: NavController, viewModel: DiscoveryDetailViewModel, itemId: String?) {

    // 1. Déclenche le chargement de l'élément unique
    LaunchedEffect(itemId) {
        viewModel.loadDiscovery(itemId)
    }

    // 2. Collecte l'état de l'élément unique
    val item by viewModel.discovery.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {},
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Section de gauche (Icône de retour et Titre)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { navController.navigate("journal") }
                                        .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.awake),
                                        contentDescription = "Leaf Icon (Back)",
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Text("Discovery Details")
                            }

                            // Section de droite (Icônes de Partage et Utilisateur)
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (item != null) {
                                    // Bouton Partage
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { shareDiscoveryIntent(context, item!!) }
                                            .background(MaterialTheme.colorScheme.background),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.share),
                                            contentDescription = "Share the Discovery",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // User Icon
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.user),
                                        contentDescription = "User Icon",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Divider(
                    modifier = Modifier.padding(top = 10.dp),
                    thickness = 5.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        })
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ){
            // Contenu principal de la carte
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        //Gère l'état de chargement/non trouvé
                        if (item == null) {
                            Text("Discovery not found or still loading...")
                            return@Column
                        }

                        val discoveryItem = item!!

                        // Image
                        if (discoveryItem.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = discoveryItem.imageUrl,
                                contentDescription = discoveryItem.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nom de la découverte (Bloc 1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {}
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.leaf2),
                                    contentDescription = "Leaf Icon",
                                    modifier = Modifier.size(30.dp)
                                )

                            }
                            Text(text = discoveryItem.name, style = MaterialTheme.typography.titleLarge)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        //Date de la découverte (Bloc 2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {}
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Leaf Icon",
                                    modifier = Modifier.size(30.dp)
                                )

                            }
                            //Convertir Long discoveryItem.timestamp en date lisible
                            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(Date(discoveryItem.timestamp))
                            Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        //Description / Fait IA
                        Text(text = discoveryItem.fact, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }


            //Bouton Delete
            Button(
                onClick = {
                    if (item != null) {
                        //On appelle la fonction sans argument.
                        viewModel.deleteCurrentDiscovery()
                        navController.navigate("journal") // Navigation après la suppression
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0XFFe05252)
                ),
                shape = Shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    "Delete",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
