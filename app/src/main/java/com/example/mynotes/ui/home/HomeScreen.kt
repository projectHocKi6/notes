package com.example.mynotes.ui.home


import android.content.Context
import android.content.pm.PackageManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.AppViewModelProvider
import com.example.mynotes.AuthManager
import com.example.mynotes.HomeBar
import com.example.mynotes.R
import com.example.mynotes.SortBar
import com.example.mynotes.ToolsBar
import com.example.mynotes.data.BiometricPromptManager
import com.example.mynotes.data.BiometricResult
import com.example.mynotes.data.Note
import kotlinx.coroutines.launch
import android.net.ConnectivityManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import com.example.mynotes.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    activity: AppCompatActivity,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToItemEntry: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToSearch: () -> Unit,
    logout: () -> Unit,
    login: () -> Unit,
    navigateToDrawing: () -> Unit,
    navigateToFileReader:()->Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollSortBar = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    // Optimize scroll button visibility logic using derivedStateOf
    val showGoTopBtn by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0
        }
    }

    RequestNotificationPermission()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuNavDrawer(
                activity,
                logout,
                login,
                viewModel
            ) { scope.launch { drawerState.close() } }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    HomeBar(
                        viewModel.currentNotesStream,
                        homeUiState.quantity,
                        scrollBehavior = scrollBehavior
                    )
                    ToolsBar(
                        homeUiState,
                        viewModel,
                        navigateToSearch,
                        auth = AuthManager,
                        dataSyn = {
                            scope.launch {
                                viewModel.dataSync()
                                Toast.makeText(activity, "Sao lưu thành công!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        openDrawer = { scope.launch { drawerState.open() } },
                        navigateToDrawing = {navigateToDrawing()},
                        navigateToFileReader = {navigateToFileReader()}
                    )
                    SortBar(homeUiState, viewModel, scrollSortBar)
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = navigateToItemEntry,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Add new note"
                    )
                }
            },
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .nestedScroll(scrollSortBar.nestedScrollConnection)
        ) { contentPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Box(modifier.fillMaxSize()) {
                    ListGridNote(
                        viewModel = viewModel,
                        uiState = homeUiState,
                        onItemClick = navigateToEdit,
                        gridState = gridState,
                    )

                    // Only show the button when needed - with animation
                    AnimatedVisibility(
                        visible = showGoTopBtn,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        FloatingActionButton(
                            onClick = { scope.launch { gridState.animateScrollToItem(0) } },
                            shape = RoundedCornerShape(50),
                            containerColor = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(bottom = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Scroll to top",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Show delete button when items are selected
                    val isSelected = viewModel.listSelectedNotes
                    if (isSelected.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    viewModel.delSelectedNotes()
                                }
                            },
                            Modifier.align(Alignment.BottomCenter)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                tint = Color.Red,
                                contentDescription = "Delete notes",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListGridNote(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    gridState: LazyGridState
) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val noteList = uiState.noteList.value

        if (noteList.isEmpty()) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Không có ghi chú!",
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                )
            }
        } else {
            // Cache this calculation
            val ratio = remember(uiState.colWidth.value) {
                if (uiState.colWidth.value == 200.dp) 1.3f else 0.7f
            }

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(uiState.colWidth.value),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = noteList,
                    key = { it.id }
                ) { note ->
                    key(note.id) {  // Ensure recomposition only happens when needed
                        NotesCard(
                            viewModel = viewModel,
                            onItemClick = onItemClick,
                            note = note,
                            modifier = modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .aspectRatio(ratio)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun NotesCard(
    viewModel: HomeViewModel,
    onItemClick: (Int) -> Unit,
    note: Note,
    modifier: Modifier
) {
    val isSelected = viewModel.listSelectedNotes.contains(note.id)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { viewModel.toggleSelection(note.id) },
                        onTap = { onItemClick(note.id) }
                    )
                },
            colors = if (note.coverOn) CardDefaults.cardColors(MaterialTheme.colorScheme.background) else CardDefaults.cardColors(
                MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = if (note.coverOn) CardDefaults.cardElevation(defaultElevation = 0.dp) else CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),

            ) {
            val context = LocalContext.current
            val coverResId =
                context.resources.getIdentifier(note.cover, "drawable", context.packageName)
            Box(
                Modifier
                    .fillMaxSize()
            ) {
                if (note.coverOn && note.cover.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = coverResId),
                        contentDescription = "Cover Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                    )
                } else {
                    Text(
                        note.context,
                        Modifier.padding(8.dp),
                        maxLines = 7
                    )
                }

                if (isSelected) {
                    RadioButton(
                        selected = true,
                        onClick = { viewModel.toggleSelection(note.id) },
                        colors = RadioButtonDefaults.colors(colorResource(id = R.color.radioBtn))
                    )
                }
            }


        }
        Text(
            note.title,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row {
            Text(note.day, fontWeight = FontWeight.Light, textAlign = TextAlign.Center)

            if (note.status == 1) {
                Icon(
                    imageVector = Icons.Default.Star,
                    tint = Color.Yellow,
                    contentDescription = "Add new note",
                    modifier = Modifier
                        .size(15.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }

}

@Composable
fun MenuNavDrawer(
    activity: AppCompatActivity,
    logout: () -> Unit,
    login: () -> Unit,
    viewModel: HomeViewModel,
    closeDrawer: () -> Unit
) {
    val promptManager by lazy { BiometricPromptManager(activity) }
    val biometricResult by promptManager.promResults.collectAsState(initial = null)
    var isLoggedIn by remember { mutableStateOf(AuthManager.isUserLoggedIn()) }
    Box(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues())
            .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            .fillMaxHeight()
            .fillMaxWidth(fraction = 0.9f)
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Menu",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            MenuItem(
                text = "Tất cả ghi chú",
                icon =
                Icons.Default.Home,
                onClick = {
                    viewModel.updateStream(0)
                    closeDrawer()
                },
            )
            MenuItem(
                text = "Yêu thích",
                icon =
                Icons.Default.Star,
                onClick = {
                    viewModel.updateStream(1)
                    closeDrawer()
                },
            )
            MenuItem(
                text = "Bảo mật",
                icon =
                Icons.Default.Lock,
                onClick = {
                    promptManager.showBiometricPrompt(
                        title = "Xác thực danh tính",
                        description = ""
                    )
                },
            )

            LaunchedEffect(biometricResult) {
                if (biometricResult is BiometricResult.AuthenticationSuccess) {
                    viewModel.updateStream(-1)
                    closeDrawer()
                } else if (biometricResult is BiometricResult.AuthenticationError ||
                    biometricResult is BiometricResult.AuthenticationFailed
                ) {
                    Toast.makeText(
                        activity,
                        "Xác thực thất bại, Vui lòng thử lại.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (biometricResult is BiometricResult.AuthenticationNotSet) {
                    Toast.makeText(
                        activity,
                        "Bạn chưa thiết lập xác thực sinh trắc học.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (biometricResult is BiometricResult.FeatureUnavailable) {
                    Toast.makeText(
                        activity,
                        "Thiết bị của bạn không hỗ trợ sinh trắc học.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (biometricResult is BiometricResult.HardwareUnavailable) {
                    Toast.makeText(
                        activity,
                        "Cảm biến sinh trắc học không khả dụng.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            HorizontalDivider()
            val currentContext = LocalContext.current
            var isNetworkAvailable by rememberSaveable {
                mutableStateOf(
                    NetworkUtils.isNetworkAvailable(
                        context = currentContext
                    )
                )
            }
            LaunchedEffect(Unit) {
                NetworkUtils.isNetworkAvailableFlow(currentContext).collect { isConnected ->
                    isNetworkAvailable = isConnected
                }
            }
            if (isNetworkAvailable) {
                MenuItem(
                    text = if (isLoggedIn) "Đăng xuất" else "Đăng nhập",
                    icon = if (isLoggedIn) Icons.Default.ExitToApp else Icons.Default.AccountCircle,
                    onClick = {
                        if (isLoggedIn) {
                            logout()
                            isLoggedIn = false
                        } else {
                            login()
                            isLoggedIn = true
                        }
                    }
                )
            }


        }
    }
}

@Composable
fun MenuItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(text, color = MaterialTheme.colorScheme.onPrimary) },
        icon = {
            Icon(
                imageVector = icon, contentDescription = "",
                Modifier.size(28.dp)
            )
        },
        onClick = onClick,
        selected = false
    )
}

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Notification", "Quyền thông báo đã được cấp!")
        } else {
            Log.d("Notification", "Quyền thông báo bị từ chối!")
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

