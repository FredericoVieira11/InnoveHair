package com.example.innovehair

import android.app.Application
import com.google.firebase.FirebaseOptions
import java.io.InputStream

class MyApplication : Application() {
// deixar tar por enquanto. O admin agr so vai poder criar novos users sem usar o admin firebase
    // por algum motivo, adicionando a dependencia do admin obtenho duplicated classes, mas possivelmente
    // seria a unica maneira de conseguir aceder ao setCredentials a seguir ao .Builder()
    override fun onCreate() {
        super.onCreate()
        val serviceAccount: InputStream = resources.openRawResource(R.raw.service_key_firebase)
        val options = FirebaseOptions.Builder()
    }
}