package com.caretail.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.caretail.app.ui.navigation.CareTailRoute
import com.caretail.app.ui.navigation.MainBottomNavRoutes
import com.caretail.app.ui.theme.CareTailAccent
import com.caretail.app.ui.theme.CareTailAccentSoft
import com.caretail.app.ui.theme.CareTailBackground
import com.caretail.app.ui.theme.CareTailBlue
import com.caretail.app.ui.theme.CareTailCard
import com.caretail.app.ui.theme.CareTailChipBackground
import com.caretail.app.ui.theme.CareTailDivider
import com.caretail.app.ui.theme.CareTailPrimary
import com.caretail.app.ui.theme.CareTailPrimaryDark
import com.caretail.app.ui.theme.CareTailTextPrimary
import com.caretail.app.ui.theme.CareTailTextSecondary
import com.caretail.app.ui.theme.CareTailWarmSurface

@Composable
fun CareTailScaffold(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedBottomRoute: String? = currentRoute,
    showBottomBar: Boolean = true,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CareTailBackground,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        bottomBar = {
            if (showBottomBar) {
                CareTailBottomNav(
                    selectedRoute = selectedBottomRoute,
                    onNavigate = onNavigate,
                )
            }
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareTailTopBar(
    title: String = "CareTail",
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    showAvatar: Boolean = false,
    showSettings: Boolean = false,
    showMenu: Boolean = false,
    onSettings: () -> Unit = {},
    onMenu: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CareTailBackground,
            titleContentColor = CareTailPrimaryDark,
            navigationIconContentColor = CareTailTextPrimary,
            actionIconContentColor = CareTailPrimaryDark,
        ),
        navigationIcon = {
            when {
                showBack -> IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
                showAvatar -> Box(Modifier.padding(start = 16.dp)) {
                    PetAvatar(name = "CareTail", size = 40.dp, backgroundColor = CareTailWarmSurface)
                }
            }
        },
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CareTailPrimaryDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            when {
                showSettings -> IconButton(onClick = onSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                }
                showMenu -> IconButton(onClick = onMenu) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More")
                }
                else -> Spacer(Modifier.size(48.dp))
            }
        },
    )
}

@Composable
fun CareTailBottomNav(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
) {
    NavigationBar(
        containerColor = CareTailCard,
        tonalElevation = 8.dp,
    ) {
        MainBottomNavRoutes.forEach { destination ->
            val selected = selectedRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.bottomNavIcon(),
                        contentDescription = destination.label,
                    )
                },
                label = { Text(destination.label, maxLines = 1) },
            )
        }
    }
}

@Composable
fun CareTailCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CareTailCard,
    contentPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

@Composable
fun PrimaryCoralButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CareTailAccent,
            contentColor = Color.White,
            disabledContainerColor = CareTailAccent.copy(alpha = 0.42f),
            disabledContentColor = Color.White.copy(alpha = 0.9f),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, disabledElevation = 0.dp),
    ) {
        ButtonContent(
            text = text,
            loading = loading,
            loadingColor = Color.White,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
fun careTailOutlinedTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = CareTailTextPrimary,
    unfocusedTextColor = CareTailTextPrimary,
    disabledTextColor = CareTailTextSecondary,
    errorTextColor = CareTailTextPrimary,
    focusedContainerColor = CareTailCard,
    unfocusedContainerColor = CareTailCard,
    disabledContainerColor = CareTailCard,
    errorContainerColor = CareTailCard,
    cursorColor = CareTailPrimary,
    errorCursorColor = CareTailAccent,
    focusedBorderColor = CareTailPrimary,
    unfocusedBorderColor = Color(0xFFD1D5DB),
    disabledBorderColor = CareTailDivider,
    errorBorderColor = CareTailAccent,
    focusedLabelColor = CareTailPrimaryDark,
    unfocusedLabelColor = CareTailTextSecondary,
    disabledLabelColor = CareTailTextSecondary.copy(alpha = 0.7f),
    errorLabelColor = CareTailAccent,
    focusedPlaceholderColor = CareTailTextSecondary.copy(alpha = 0.75f),
    unfocusedPlaceholderColor = CareTailTextSecondary.copy(alpha = 0.75f),
    disabledPlaceholderColor = CareTailTextSecondary.copy(alpha = 0.5f),
    errorPlaceholderColor = CareTailTextSecondary.copy(alpha = 0.75f),
)

@Composable
fun PetAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    backgroundColor: Color = CareTailPrimary,
    icon: ImageVector = Icons.Rounded.Pets,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$name avatar",
            tint = if (backgroundColor == CareTailPrimary) CareTailTextPrimary else CareTailPrimaryDark,
            modifier = Modifier.size(size * 0.52f),
        )
    }
}

@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CareTailPrimary.copy(alpha = 0.16f),
    contentColor: Color = CareTailPrimaryDark,
    icon: ImageVector? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, color = contentColor)
        }
    }
}

@Composable
fun ReminderTypeChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    SelectableCareTailChip(
        text = text,
        selected = selected,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        onClick = onClick,
    )
}

@Composable
fun CareTailChip(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
) {
    val chipModifier = if (onClick != null) {
        modifier.clickable(enabled = enabled, onClick = onClick)
    } else {
        modifier
    }
    Surface(
        modifier = chipModifier,
        shape = RoundedCornerShape(999.dp),
        color = CareTailChipBackground,
        contentColor = CareTailTextPrimary,
        border = BorderStroke(1.dp, CareTailDivider),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = CareTailPrimaryDark, modifier = Modifier.size(16.dp))
            }
            Text(
                text = text,
                color = if (enabled) CareTailTextPrimary else CareTailTextSecondary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun SelectableCareTailChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    val backgroundColor = if (selected) CareTailPrimary.copy(alpha = 0.24f) else CareTailChipBackground
    val contentColor = if (selected) CareTailPrimaryDark else CareTailTextPrimary
    Surface(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) CareTailPrimaryDark else CareTailDivider,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            when {
                selected -> Icon(Icons.Rounded.Check, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                leadingIcon != null -> Icon(leadingIcon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            }
            Text(
                text = text,
                color = if (enabled) contentColor else CareTailTextSecondary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    CareTailCard(
        modifier = modifier.clickable(onClick = onClick),
        contentPadding = 14.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CareTailPrimary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = title, tint = CareTailPrimaryDark, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = CareTailTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = CareTailPrimaryDark, modifier = Modifier.size(23.dp))
            }
            Text(title, style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        }
        if (actionText != null) {
            TextButton(onClick = onAction) {
                Text(actionText, color = CareTailPrimaryDark, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun PremiumBenefitRow(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CareTailPrimary.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = CareTailPrimaryDark, modifier = Modifier.size(20.dp))
        }
        Text(text, style = MaterialTheme.typography.bodyLarge, color = CareTailTextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CareTailBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CareTailAccent,
    contentColor: Color = Color.White,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
        )
    }
}

@Composable
fun PricingCard(
    title: String,
    price: String,
    detail: String? = null,
    selected: Boolean,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(124.dp)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) CareTailPrimary else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(22.dp),
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFE9FAF8) else CareTailCard,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 2.dp),
        shape = RoundedCornerShape(22.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 14.dp,
                        top = if (badge != null) 34.dp else 16.dp,
                        end = 14.dp,
                        bottom = 14.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = CareTailTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    price,
                    style = MaterialTheme.typography.titleLarge,
                    color = CareTailTextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                )
                if (detail != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        detail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CareTailPrimaryDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                    )
                }
            }
            if (badge != null) {
                CareTailBadge(
                    text = badge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp),
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CareTailAccentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = CareTailAccent, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = CareTailTextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = CareTailTextSecondary)
        }
        trailing?.invoke()
    }
}

@Composable
fun CoralFab(
    onClick: () -> Unit,
    contentDescription: String = "Add",
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = CareTailAccent,
        contentColor = Color.White,
        shape = RoundedCornerShape(22.dp),
    ) {
        Icon(Icons.Rounded.Add, contentDescription = contentDescription)
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    CareTailCard(modifier = modifier.fillMaxWidth(), backgroundColor = CareTailWarmSurface) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = CareTailTextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, color = CareTailTextSecondary)
        if (actionText != null) {
            Spacer(Modifier.height(18.dp))
            PrimaryCoralButton(text = actionText, onClick = onAction)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    SecondaryCareTailButton(
        text = text,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        onClick = onClick,
    )
}

@Composable
fun SecondaryCareTailButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CareTailCard,
            contentColor = CareTailPrimaryDark,
            disabledContainerColor = CareTailCard,
            disabledContentColor = CareTailTextSecondary.copy(alpha = 0.65f),
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) CareTailPrimaryDark.copy(alpha = 0.45f) else CareTailDivider,
        ),
    ) {
        ButtonContent(text = text, leadingIcon = leadingIcon)
    }
}

@Composable
fun TextActionButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = CareTailPrimaryDark,
            disabledContentColor = CareTailTextSecondary.copy(alpha = 0.65f),
        ),
    ) {
        ButtonContent(text = text, leadingIcon = leadingIcon)
    }
}

@Composable
fun DestructiveCareTailButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    filled: Boolean = false,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    if (filled) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CareTailAccent,
                contentColor = Color.White,
                disabledContainerColor = CareTailAccent.copy(alpha = 0.36f),
                disabledContentColor = Color.White.copy(alpha = 0.86f),
            ),
        ) {
            ButtonContent(text = text, leadingIcon = leadingIcon)
        }
    } else {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(48.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = CareTailAccent,
                disabledContentColor = CareTailTextSecondary.copy(alpha = 0.65f),
            ),
        ) {
            ButtonContent(text = text, leadingIcon = leadingIcon)
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean = false,
    loadingColor: Color = CareTailPrimaryDark,
    leadingIcon: ImageVector? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        when {
            loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = loadingColor,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(10.dp))
            }
            leadingIcon != null -> {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
        }
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PetImagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                Brush.verticalGradient(
                    listOf(CareTailWarmSurface, CareTailPrimary.copy(alpha = 0.12f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.Pets,
            contentDescription = null,
            tint = CareTailPrimaryDark,
            modifier = Modifier.size(96.dp),
        )
    }
}

fun CareTailRoute.bottomNavIcon(): ImageVector = when (this) {
    CareTailRoute.Home -> Icons.Rounded.Home
    CareTailRoute.Pets -> Icons.Rounded.Pets
    CareTailRoute.Reminders -> Icons.Rounded.Notifications
    CareTailRoute.Diary -> Icons.Rounded.Event
    CareTailRoute.Settings -> Icons.Rounded.Settings
    else -> Icons.Rounded.Home
}

val AddPetIcon: ImageVector = Icons.Rounded.Pets
val ReminderIcon: ImageVector = Icons.Rounded.Notifications
val HealthIcon: ImageVector = Icons.Rounded.Favorite
val DocumentIcon: ImageVector = Icons.Rounded.Description
val FolderIcon: ImageVector = Icons.Rounded.Folder
val EditIcon: ImageVector = Icons.Rounded.Edit
val StarIcon: ImageVector = Icons.Rounded.Star
val DropDownIcon: ImageVector = Icons.Rounded.KeyboardArrowDown
