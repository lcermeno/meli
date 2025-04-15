# 🛍️ Qiubo Meli App

This is an Android application built in Kotlin using Jetpack Compose and Clean Architecture. It integrates with the Mercado Libre API, allowing users to authenticate and browse their published products with a modern, reactive interface.

## 🚀 Features

- 🛡️ OAuth2 login via Mercado Libre
- 🧾 Secure token storage using DataStore
- 📦 Product listing with paging support
- 🔎 In-app search with dynamic filtering
- 🌗 Material 3 design with light theme support
- 🔄 Deep link handling for secure login redirects
- 🔐 Authenticated API access using OkHttp Interceptors

## 🧭 Why Firebase Hosting?

Mercado Libre's OAuth flow requires a valid `https`-based `redirect_uri`, and it does **not support custom schemes** like `qiubo://auth` directly from a browser. To solve this:

1. We use **Firebase Hosting** to host a simple HTML redirect page.
2. After the user logs in, Mercado Libre redirects to our hosted page.
3. That page reads the `code` from the URL and redirects it back to our app via a **deep link** using `qiubo://auth?code=...`.

This allows us to comply with Mercado Libre’s OAuth 2.0 security requirements while maintaining a seamless user experience inside our Android app.

## 🧰 Tech Stack

- Kotlin
- Jetpack Compose
- Hilt (DI)
- Retrofit + Moshi
- Paging 3
- Coil (for image loading)
- Firebase Hosting
- Accompanist System UI Controller
- Postman (API test suite)

## 🛠️ Architecture

- Clean Architecture principles
- Domain layer abstraction
- Repository pattern
- MVVM + StateFlow for UI state management
- Decoupled navigation using sealed routes

## 🗝️ OAuth Flow

1. User clicks "Login with Mercado Libre"
2. Chrome Custom Tab opens the Mercado Libre login
3. After successful login, Mercado Libre redirects to a Firebase-hosted page
4. The page reads the `code` and redirects to a deep link (e.g., `qiubo://auth?code=...`)
5. The app receives the code and exchanges it for an access token

## 🧪 API Testing with Postman

This project includes a Postman collection and environment setup to test Mercado Libre's public APIs and the authentication flow used by the app.

### Folder Structure

Inside the root directory of the project, you’ll find the `/postman` folder containing:

- `MELI.postman_collection.json`: The collection of all API endpoints used in the project.
- `MELI.postman_environment.json`: A set of preconfigured variables (like `ACCESS_TOKEN`, `APP_ID`, and `BASE_URL`) for quick testing.

### How to Use

1. Open [Postman](https://www.postman.com/).
2. Go to **File > Import**.
3. Select both `MELI.postman_collection.json` and `MELI.postman_environment.json` from the `/postman` directory.
4. Choose the `MELI` environment in Postman.
5. Test endpoints such as:
    - `GET /users/me`
    - `GET /items`
    - `POST /oauth/token`

## 📸 Screenshots

_Coming soon..._

## 📦 Setup

1. Clone the repository
2. Replace OAuth `client_id` and `redirect_uri` with your credentials
3. Set up Firebase Hosting and upload the `redirect.html` file
4. Build and run the app on Android Studio

## 🔒 Security

Tokens are securely stored in encrypted DataStore and automatically refreshed via an `Authenticator`.

## 🤝 License

MIT License
