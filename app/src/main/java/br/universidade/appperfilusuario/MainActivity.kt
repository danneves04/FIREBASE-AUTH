package br.universidade.appperfilusuario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.universidade.appperfilusuario.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val prefs = getSharedPreferences("usuarios_prefs", Context.MODE_PRIVATE)

        val user = auth.currentUser
        val nome = user?.displayName?.takeIf { it.isNotBlank() }
            ?: prefs.getString("logado_nome", "Usuário")
        val email = user?.email?.takeIf { it.isNotBlank() }
            ?: prefs.getString("logado_email", "usuario@email.com")

        binding.txtNomeUsuario.text = nome
        binding.txtEmailUsuario.text = email

        binding.btnSair.setOnClickListener {
            prefs.edit()
                .remove("logado_email")
                .remove("logado_nome")
                .apply()

            auth.signOut()

            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Toast.makeText(this, "Você saiu da conta!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }
}
