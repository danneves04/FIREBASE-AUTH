# AppPerfilUsuario - Login e Cadastro com FirebaseUI

Aplicativo Android em Kotlin que implementa autenticação de usuários seguindo o modelo da documentação oficial do FirebaseUI (`FirebaseAuthUIActivityResultContract`, `AuthUI` e `FirebaseAuth`).

## Funcionalidades
- **Tela de Login (`LoginActivity`)**: Permite autenticar usuários existentes com E-mail/Senha ou abrir o fluxo do FirebaseUI (`AuthUI.getInstance().createSignInIntentBuilder()`).
- **Cadastro de Usuários**: Permite cadastrar novos usuários informando Nome, E-mail e Senha (`createUserWithEmailAndPassword`).
- **Tela de Perfil do Usuário (`MainActivity`)**: Exibe os dados do usuário autenticado (Nome e E-mail) e possui o botão **Sair e Voltar ao Login** (`AuthUI.getInstance().signOut(this)`), encerrando a sessão e retornando para a tela de login.

## Tecnologias Utilizadas
- Kotlin
- ViewBinding
- Firebase Authentication (`com.google.firebase:firebase-auth`)
- FirebaseUI Auth (`com.firebaseui:firebase-ui-auth`)
