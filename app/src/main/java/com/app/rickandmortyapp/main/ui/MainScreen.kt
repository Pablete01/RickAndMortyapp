package com.app.rickandmortyapp.main.ui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.rickandmortyapp.R
import com.app.rickandmortyapp.main.data.network.response.Character


@Composable
fun MainScreen(appViewModel: AppViewModel) {
    val characters = appViewModel.characters.collectAsLazyPagingItems()
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    val changeList: Boolean by appViewModel.changeList.observeAsState(initial = true)
    val activerSearchBar: Boolean by appViewModel.activeSearchBar.observeAsState(initial = true)



    Scaffold(
        topBar = {
            TopAppBarApp(
                activeSearchBar = activerSearchBar,
                viewModel = appViewModel,
                changeList = changeList,
                onCharacterSelected = {
                    selectedCharacter = it
                })
        },
        content = { it ->
            CharacterList(
                changeList = changeList,
                characters = characters,
                innerPadding = it,
                onCharacterSelected = {
                    selectedCharacter = it
                })
            if (selectedCharacter != null) {
                CharacterDetails(character = selectedCharacter!!,
                    onDismiss = { selectedCharacter = null })
            }
        },
        bottomBar = { BottomBar() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarApp(
    activeSearchBar: Boolean,
    viewModel: AppViewModel,
    changeList: Boolean,
    onCharacterSelected: (Character) -> Unit
) {

    if (activeSearchBar) {
        SearchView(viewModel, onCharacterSelected)
    }

    TopAppBar(
        title = {
            Text(
                text = "Rick & Morty Characters",
            )
        },
        colors = TopAppBarColors(
            containerColor = Color(0xFF7C3E1D),
            titleContentColor = Color.White,
            scrolledContainerColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(
                onClick = { viewModel.onActiveSearchBar() },
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp),

                ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White
                )

            }

            IconButton(
                onClick = { viewModel.onButtonClick() },
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp),

                ) {
                Image(
                    painterResource(
                        if (changeList) {
                            R.drawable.grid_view_24px
                        } else {
                            R.drawable.lists_24px
                        }
                    ),
                    contentDescription = null
                )

            }

        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    viewModel: AppViewModel,
    onCharacterSelected: (Character) -> Unit,
) {
    val searchedList: List<Character> by viewModel.searchedCharacters.observeAsState(emptyList())
    var active by remember { mutableStateOf(false) }
    val search: String by viewModel.search.observeAsState(initial = "")

    SearchBar(
        query = search,
        onQueryChange = { viewModel.getSearchedCharacters(search = it) },
        onSearch = {
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        modifier = Modifier.wrapContentHeight(),
        placeholder = { Text(text = "Search by name") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "search icon")
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (search.isNotEmpty()) {
                            viewModel.getSearchedCharacters(search = "")
                        } else {
                            viewModel.onActiveSearchBar()
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "search icon"
                )
            }
        }
    ) {
        LazyColumn {
            items(searchedList) {
                Row(
                    modifier = Modifier.clickable { onCharacterSelected(it) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(character = it)
                    Text(
                        text = it.name,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun BottomBar() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF7C3E1D))
    )
}

@Composable
fun CharacterList(
    changeList: Boolean,
    characters: LazyPagingItems<Character>,
    innerPadding: PaddingValues,
    onCharacterSelected: (Character) -> Unit,


    ) {

    if (characters.itemCount == 0) {
        ErrorContent(
            modifier = Modifier
                .padding(
                    top = 80.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxHeight()
                .fillMaxWidth(),
            characters = characters
        )
    } else {

        if (changeList) {
            LazyColumn(contentPadding = innerPadding) {
                items(characters.itemCount) { index ->
                    characters[index]?.let {
                        CardLayout(
                            changeList = changeList,
                            modifier = Modifier
                                .padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
                                .fillMaxWidth()
                                .clickable {
                                    onCharacterSelected(it)
                                },
                            character = it,
                        )
                    }
                }
                characters.apply {
                    when {
                        loadState.append is LoadState.Error ->
                            item {
                                ErrorContent(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxHeight()
                                        .fillMaxWidth(),
                                    characters = characters
                                )
                            }

                        loadState.append is LoadState.Loading -> {
                            item { LoadingContent() }
                        }

                        loadState.refresh is LoadState.Loading -> {
                            item { LoadingView() }
                        }
                    }
                }
            }

        } else {
            LazyVerticalGrid(
                modifier = Modifier.padding(top = 64.dp, bottom = 64.dp),
                columns = GridCells.Fixed(2)
            ) {
                items(characters.itemCount) { index ->
                    characters[index]?.let {
                        CardLayout(
                            changeList = changeList,
                            modifier = Modifier
                                .padding(2.dp)
                                .height(144.dp)
                                .clickable {
                                    onCharacterSelected(it)
                                },
                            character = it,

                            )
                    }
                }
                characters.apply {
                    when {
                        loadState.append is LoadState.Error ->
                            item {
                                ErrorContent(
                                    modifier = Modifier
                                        .padding(
                                            top = 16.dp,
                                            start = 16.dp,
                                            end = 16.dp,
                                            bottom = 64.dp
                                        )
                                        .fillMaxHeight()
                                        .fillMaxWidth(),
                                    characters = characters
                                )
                            }

                        loadState.append is LoadState.Loading -> {
                            item { LoadingContent() }
                        }

                        loadState.refresh is LoadState.Loading -> {
                            item { LoadingView() }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CardLayout(
    changeList: Boolean,
    modifier: Modifier,
    character: Character,

    ) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5e19f)),
        shape = RoundedCornerShape(10.dp),

        ) {
        if (changeList)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(character)
                CardBodyContent(character, modifier = modifier)
            }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(character)
                CardBodyContent(character, modifier)
            }
        }
    }
}

@Composable
fun Image(character: Character) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(character.image)
            .crossfade(true).build(),
        contentDescription = "",
        modifier = Modifier
            .padding(4.dp)
            .size(80.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(
                    2.dp, when (character.status) {
                        "Alive" -> {
                            Color.Green
                        }

                        "Dead" -> {
                            Color.Red
                        }

                        else -> (Color.Gray)
                    }
                ),
                CircleShape
            ),
        contentScale = ContentScale.Crop,

        )
}

@Composable
private fun CardBodyContent(character: Character, modifier: Modifier) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.padding(4.dp)
//            .fillMaxWidth()
    ) {
        Text(
            text = character.name,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

//        Text(
////            modifier = Modifier.padding(
////                top = 1.dp, start = 6.dp
////            ),
//            text = "${character.status} - ${character.species}",
//            color = Color.Black,
//            fontSize = 14.sp
//        )

    }
}

@Composable
fun CharacterDetails(
    character: Character,
    onDismiss: () -> Unit,

    ) {

    Dialog(properties = DialogProperties(usePlatformDefaultWidth = true),
        onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF5e19f),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(character.image)
                        .crossfade(true).build(),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(128.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                        .border(
                            BorderStroke(
                                2.dp, when (character.status) {
                                    "Alive" -> {
                                        Color.Green
                                    }

                                    "Dead" -> {
                                        Color.Red
                                    }

                                    else -> (Color.Gray)
                                }
                            ),
                            CircleShape
                        ),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = character.name, style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify

                    ), color = Color.Black, modifier = Modifier.align(Alignment.CenterHorizontally)
                )



                Box(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp)) {
                    Column {
                        Row(
                            Modifier.padding(top = 8.dp)
                        ) {
                            Box(
                                Modifier
                                    .shadow(2.dp)
                                    .padding(1.dp)
                                    .wrapContentSize()
                                    .background(Color(0xFFF3CF57))
                            ) {
                                Text(
                                    text = "Gender:",
                                    style = TextStyle(
                                        fontSize = 16.sp
                                    ),
                                    color = Color.Black,
                                )
                            }
                            Text(
                                text = character.gender,
                                style = TextStyle(
                                    fontSize = 16.sp
                                ),
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 14.dp)
                            )
                        }

                        Row(
                            Modifier
                                .padding(top = 4.dp)
                        ) {
                            Box(
                                Modifier
                                    .shadow(2.dp)
                                    .padding(1.dp)
                                    .wrapContentSize()
                                    .background(Color(0xFFF3CF57))
                            ) {
                                Text(
                                    text = "Species:",
                                    style = TextStyle(
                                        fontSize = 16.sp
                                    ),
                                    color = Color.Black,
                                )
                            }
                            Text(
                                text = character.species,
                                style = TextStyle(
                                    fontSize = 16.sp

                                ),
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp)
                            )
                        }

                        Row(
                            Modifier

                                .padding(top = 4.dp)
                        ) {
                            Box(
                                Modifier
                                    .shadow(2.dp)
                                    .padding(1.dp)
                                    .wrapContentSize()
                                    .background(Color(0xFFF3CF57))
                            ) {
                                Text(
                                    text = "Status:",
                                    style = TextStyle(
                                        fontSize = 16.sp
                                    ),
                                    color = Color.Black,
                                )
                            }
                            Text(
                                text = character.status,
                                style = TextStyle(
                                    fontSize = 16.sp,

                                    ),
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 18.dp)
                            )
                        }
                        Row(
                            Modifier
                                .padding(top = 4.dp)
                        ) {
                            Box(
                                Modifier
                                    .shadow(2.dp)
                                    .padding(1.dp)
                                    .wrapContentSize()
                                    .background(Color(0xFFF3CF57))
                            ) {
                                Text(
                                    text = "Type:",
                                    style = TextStyle(
                                        fontSize = 16.sp
                                    ),
                                    color = Color.Black,
                                )
                            }
                            Text(
                                text = if (character.type == "") {
                                    "?"
                                } else {
                                    character.type
                                },
                                style = TextStyle(
                                    fontSize = 16.sp,

                                    ),
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 29.dp)
                            )
                        }
                        Row(
                            Modifier

                                .padding(top = 4.dp)
                        ) {
                            Box(
                                Modifier
                                    .shadow(2.dp)
                                    .padding(1.dp)
                                    .wrapContentSize()
                                    .background(Color(0xFFF3CF57))
                            ) {
                                Text(
                                    text = "Origin:",
                                    style = TextStyle(
                                        fontSize = 16.sp

                                    ),
                                    color = Color.Black,
                                )
                            }
                            Text(
                                text = character.origin.name,
                                style = TextStyle(
                                    fontSize = 16.sp,

                                    ),
                                color = Color.Black,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 20.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ErrorContent(modifier: Modifier, characters: LazyPagingItems<Character>) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(id = R.drawable.error),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )
        Text(
            text = "Oops! Something went wrong.",
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = { characters.retry() }) {
            Text(
                text = "Try Again"
            )
        }
    }
}

@Composable
fun LoadingContent() {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}


