package com.example.midterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.midterm.ui.AdminProductScreen
import com.example.midterm.ui.AdminUserScreen
import com.example.midterm.ui.LoginScreen
import com.example.midterm.ui.SignUpScreen
import com.example.midterm.ui.UserProductScreen
import com.example.midterm.ui.theme.MidtermTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MidtermTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        composable("admin_screen") {
            AdminProductScreen(navController = navController)
        }
        composable("user_screen") {
            UserProductScreen(navController = navController)
        }
        composable("manage_users") {
            AdminUserScreen(navController = navController)
        }
    }
}
