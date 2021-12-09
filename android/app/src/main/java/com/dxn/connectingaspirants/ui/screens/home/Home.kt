package com.dxn.connectingaspirants.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dxn.connectingaspirants.ui.components.HeadingText
import com.dxn.connectingaspirants.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun Home(
    viewModel: HomeViewModel,
    currentUser: FirebaseUser,
    navController: NavController,
    signOut: () -> Unit
) {

    val pagerState = rememberPagerState()
    val pages = listOf("Explore", "Feeds")
    val scope = rememberCoroutineScope()

    val isLoading by remember { viewModel.isLoading }
    val users by remember { viewModel.users }
    var isDialogVisible by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.background) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeadingText(text = "Hello!\n${currentUser.displayName!!}", maxLines = 2)
                    Row {
                        IconButton(
                            modifier = Modifier.size(40.dp),
                            onClick = { isDialogVisible = true }) {
                            Image(
                                modifier = Modifier.clip(CircleShape),
                                painter = rememberImagePainter(data = currentUser.photoUrl),
                                contentDescription = "user profile",
                            )
                        }
                        IconButton(
                            modifier = Modifier.size(40.dp),
                            onClick = { navController.navigate("chats_screen") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chat),
                                contentDescription = "chats",
                                tint = MaterialTheme.colors.primary,
                            )
                        }
                    }
                }
            }

        },
        bottomBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                }
            ) {
                pages.forEachIndexed { index, s ->
                    Tab(
                        text = { Text(s) },
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                    )
                }
            }
        }
    ) {
        HorizontalPager(count = 2, state = pagerState) { page ->
            when (page) {
                0 -> {
                    Explore(currentUser.uid, users, isLoading, navController) {
                        viewModel.loadUsers()
                    }
                }
                1 -> {
                    Feeds()
                }
            }
        }
        if (isDialogVisible) {
            AlertDialog(
                onDismissRequest = { isDialogVisible = false },
                confirmButton = {
                    TextButton(onClick = signOut) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDialogVisible = false }) {
                        Text(text = "No")
                    }
                },
                title = {
                    Text(text = "Sign out?")
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground
            )
        }
    }
}