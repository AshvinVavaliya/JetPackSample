package com.simform.samplejetpackcompose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simform.samplejetpackcompose.ui.theme.ShimmerEffectsTheme
import com.simform.samplejetpackcompose.R
import com.simform.simmereffect.dynamicShimmer
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShimmerEffectsTheme {
                AppScreen()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, name = "Light Mode Preview")
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark Mode Preview"
)
@Composable
fun AppScreenPreview() {
    ShimmerEffectsTheme {
        AppScreen()
    }
}

@Composable
fun AppScreen() {
    var showOnboarding by rememberSaveable { mutableStateOf(true) }
    val items = remember { mutableStateListOf(*List(1) { "Item $it" }.toTypedArray()) }
    var itemCount by remember { mutableStateOf(items.size) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    Scaffold(topBar = {
        AppTopBar()
    }, bottomBar = {
        AppBottomBar(drawerState) { isDrawerOpen = true }
    }, floatingActionButton = {
        if (!showOnboarding) {
            AddItemFloatingButton(onClick = {
                items.add("Item $itemCount")
                itemCount++
            })
        }
    }) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .dynamicShimmer(
                    colors = listOf(
                        Color.Cyan.copy(alpha = 0.6f),
                        Color.Magenta.copy(alpha = 0.2f),
                        Color.Cyan.copy(alpha = 0.6f)
                    ), animationDuration = 1500, shimmerWidth = 300f, tileMode = TileMode.Repeated
                ),
            color = MaterialTheme.colorScheme.background,
        ) {
            if (showOnboarding) {
                OnboardingScreen { showOnboarding = false }
            } else {
                ItemList(items)
            }
        }
    }

    if (isDrawerOpen) {
        DrawerContent(drawerState) { isDrawerOpen = false }
    }
}

@Composable
fun AppTopBar() {
    TopAppBar(
        title = { Text(text = "Sample") },
        navigationIcon = {
            IconButton(onClick = { /* handle click */ }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = { /* handle click */ }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
        backgroundColor = MaterialTheme.colorScheme.primary,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun AddItemFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = "Add Item")
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to the App!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun ItemList(items: List<String>) {
    var expandedItem by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = items) { item ->
            ItemCard(name = item,
                isExpanded = item == expandedItem,
                onExpand = { expandedItem = if (expandedItem == item) null else item })
        }
    }
}

@Composable
fun ItemCard(
    name: String, isExpanded: Boolean, onExpand: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ItemDialog(name) { showDialog = false }
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { showDialog = true }) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text("Hello, ")
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
                if (isExpanded) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = "This is the expanded content for $name. It provides more details."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsChip()
                }
            }
            IconButton(onClick = onExpand) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemDialog(name: String, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Info, contentDescription = null) },
        title = { Text(text = name) },
        text = {
            Text(stringResource(R.string.dialog_contain, name, name))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss))
            }
        })
}

@Composable
fun SettingsChip() {
    AssistChip(onClick = { /* Handle settings */ }, label = {
        Text("Settings", color = MaterialTheme.colorScheme.onPrimary)
    }, leadingIcon = {
        Icon(
            Icons.Filled.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }, colors = AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        labelColor = MaterialTheme.colorScheme.onPrimary,
        leadingIconContentColor = MaterialTheme.colorScheme.onPrimary
    ), border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
    )
}

@Composable
fun AppBottomBar(drawerState: BottomDrawerState, onDrawerOpen: () -> Unit) {
    var selectedIcon by remember { mutableStateOf(SelectedIcon.NONE) }
    val scope = rememberCoroutineScope()

    BottomAppBar(windowInsets = AppBarDefaults.bottomAppBarWindowInsets) {
        IconButton(
            onClick = {
                selectedIcon = SelectedIcon.MENU
                onDrawerOpen()
                scope.launch { drawerState.open() }
            },
            modifier = Modifier
                .padding(16.dp)
                .background(getBackgroundColor(SelectedIcon.MENU, selectedIcon))
                .scale(getScale(SelectedIcon.MENU, selectedIcon))
        ) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu")
        }

        Spacer(Modifier.weight(1f, true))

        IconButton(
            onClick = { selectedIcon = SelectedIcon.FAVORITE },
            modifier = Modifier
                .padding(16.dp)
                .background(getBackgroundColor(SelectedIcon.FAVORITE, selectedIcon))
                .scale(getScale(SelectedIcon.FAVORITE, selectedIcon))
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
        }

        IconButton(
            onClick = { selectedIcon = SelectedIcon.CALL },
            modifier = Modifier
                .padding(16.dp)
                .background(getBackgroundColor(SelectedIcon.CALL, selectedIcon))
                .scale(getScale(SelectedIcon.CALL, selectedIcon))
        ) {
            Icon(Icons.Filled.Call, contentDescription = "Call")
        }
    }
}

@Composable
private fun getBackgroundColor(icon: SelectedIcon, selectedIcon: SelectedIcon): Color {
    return if (icon == selectedIcon) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primaryContainer
}

@Composable
private fun getScale(icon: SelectedIcon, selectedIcon: SelectedIcon): Float {
    return if (icon == selectedIcon) 1.5f else 1f
}

@Composable
fun DrawerContent(drawerState: BottomDrawerState, onDrawerClose: () -> Unit) {
    val scope = rememberCoroutineScope()

    BottomDrawer(drawerState = drawerState, drawerContent = {
        Text(text = "Drawer content", modifier = Modifier.padding(16.dp))
        Button(onClick = {
            scope.launch { drawerState.close() }
            onDrawerClose()
        }) {
            Text(text = "Close Drawer")
        }
    }) {}
}

enum class SelectedIcon {
    NONE, MENU, FAVORITE, CALL
}
