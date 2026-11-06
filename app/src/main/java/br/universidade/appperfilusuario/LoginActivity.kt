package br.universidade.appperfilusuario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.universidade.appperfilusuario.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val prefs = getSharedPreferences("usuarios_prefs", Context.MODE_PRIVATE)
        val emailLogado = prefs.getString("logado_email", null)
        if (auth.currentUser != null || emailLogado != null) {
            abrirTelaPerfil()
            return
        }

        binding.btnEntrar.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val senha = binding.editSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha o e-mail e a senha!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        prefs.edit()
                            .putString("logado_email", user?.email ?: email)
                            .putString("logado_nome", user?.displayName ?: prefs.getString("nome_$email", "Usuário"))
                            .apply()
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        abrirTelaPerfil()
                    } else {
                        val senhaSalva = prefs.getString("senha_$email", null)
                        if (senhaSalva != null && senhaSalva == senha) {
                            val nomeSalvo = prefs.getString("nome_$email", "Usuário")
                            prefs.edit()
                                .putString("logado_email", email)
                                .putString("logado_nome", nomeSalvo)
                                .apply()
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            abrirTelaPerfil()
                        } else {
                            Toast.makeText(
                                this,
                                "Usuário ou senha inválidos. Cadastre-se primeiro!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.editNome.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val senha = binding.editSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Informe o e-mail e a senha para cadastrar!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                binding.editSenha.error = "A senha deve ter pelo menos 6 caracteres"
                return@setOnClickListener
            }

            val nomeFinal = if (nome.isNotEmpty()) nome else email.substringBefore("@")

            prefs.edit()
                .putString("senha_$email", senha)
                .putString("nome_$email", nomeFinal)
                .putString("logado_email", email)
                .putString("logado_nome", nomeFinal)
                .apply()

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nomeFinal)
                            .build()
                        auth.currentUser?.updateProfile(profileUpdates)
                    }
                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    abrirTelaPerfil()
                }
        }

        binding.btnFirebaseUi.setOnClickListener {
            iniciarLoginFirebaseUI()
        }
    }

    private fun iniciarLoginFirebaseUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            val prefs = getSharedPreferences("usuarios_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("logado_email", user?.email ?: "usuario@email.com")
                .putString("logado_nome", user?.displayName ?: "Usuário")
                .apply()
            abrirTelaPerfil()
        } else {
            Toast.makeText(this, "Autenticação cancelada ou requer configuração do Firebase.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirTelaPerfil() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
