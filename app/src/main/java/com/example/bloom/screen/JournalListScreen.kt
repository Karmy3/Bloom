package com.example.bloom.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bloom.ui.theme.Shapes
import com.example.bloom.viewModel.JournalViewModel
import com.example.bloom.R
import com.example.bloom.model.model.entities.Discovery
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalListScreen(
    navController: NavController,
    viewModel: JournalViewModel
) {
    val discoveries: List<Discovery> by viewModel.filteredDiscoveries.collectAsState(initial = emptyList())
    var isSearching by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (isSearching) {
                            //AFFICHAGE DU CHAMP DE RECHERCHE
                            TextField(
                                value = searchQuery,
                                onValueChange = viewModel::onSearchQueryChanged,
                                placeholder = { Text("Rechercher dans les découvertes...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                        } else {
                            Text("Mon Journal de Découvertes", style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icône 1 (Feuille)
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.leaf),
                                    contentDescription = "Leaf Icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            //Icône 2 (Recherche) - Clicable pour basculer l'état
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    // Action : Basculer l'état de recherche
                                    .clickable { isSearching = !isSearching }
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                //Icône changeante : loupe quand masqué, croix quand affiché
                                Image(
                                    painter = painterResource(
                                        id = if (isSearching) R.drawable.close_icon else R.drawable.search
                                    ),
                                    contentDescription = "Search/Close Icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                Divider(
                    modifier = Modifier.padding(top = 10.dp),
                    thickness = 5.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Bouton Ajouter (+)
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { navController.navigate("addDiscovery") },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Add Icon",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        //Affichage des découvertes
        if (discoveries.isEmpty() && searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No discoveries recorded. Use the '+' button to start.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (discoveries.isEmpty() && searchQuery.isNotBlank()) {
            //Afficher un message spécifique si la recherche ne donne rien
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No results found \"$searchQuery\".",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                items(discoveries) { item ->
                    // Appel de la fonction de composant de liste d'éléments
                    DiscoveryListItem(navController = navController, item = item)
                }
            }
        }
    }
}


@Composable
fun DiscoveryListItem(navController: NavController, item: Discovery) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("detail/${item.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = Shapes.large
                )
        ) {
            // Image
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ligne Nom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.leaf2),
                        contentDescription = "Leaf Icon",
                        modifier = Modifier.size(30.dp)
                    )

                }
                Text(text = item.name, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Ligne Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Date Icon",
                        modifier = Modifier.size(30.dp)
                    )

                    // Convertir Long item.timestamp en date lisible
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(item.timestamp))
                    Text(text = formattedDate, style = MaterialTheme.typography.bodyMedium)

                }

            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.fact, style = MaterialTheme.typography.bodyMedium)
        }
    }
}