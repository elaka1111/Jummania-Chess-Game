<p align="center">
   <img src="https://github.com/user-attachments/assets/b17218f0-d731-4c77-afb0-90ae43cec929" alt="App Icon" width="666">
   <br>
   <img src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat" alt="API Level 16 and above"/>
   <a href="https://jitpack.io/#Jumman04/Jummania-Chess-Game">
   <img src="https://jitpack.io/v/Jumman04/Jummania-Chess-Game.svg" alt="JitPack Version"/>
   </a>
   <a href="https://github.com/Jumman04/Jummania-Chess-Game/network/members">
   <img src="https://img.shields.io/github/forks/Jumman04/Jummania-Chess-Game" alt="GitHub Forks"/>
   </a>
   <a href="https://github.com/Jumman04/Jummania-Chess-Game/stargazers">
   <img src="https://img.shields.io/github/stars/Jumman04/Jummania-Chess-Game" alt="GitHub Stars"/>
   </a>
   <a href="https://github.com/Jumman04/Jummania-Chess-Game/blob/master/LICENSE.md">
    <img src="https://img.shields.io/github/license/Jumman04/Jummania-Chess-Game.svg" alt="GitHub License"/>
   </a>
</p>

# ♟️ ChessView - Customizable Chess Game Library for Android

**ChessView** is a fully-featured chess game library for Android, built with Kotlin. It delivers
smooth interactions, realistic piece movements, and a highly customizable interface — including
board themes, piece styles, sound effects, and stroke options.

It includes complete gameplay logic: legal move validation, turn-based play, and piece movement
rules. You can play against an AI powered by **Gemini**, or extend it for **multiplayer support**
with the same seamless experience.

Designed for easy integration via XML or code, **ChessView** brings full chess functionality to any
Android app with just a few lines of code.

## Features

- Customizable board square colors
- Filled or outlined chess piece styles
- Multiple piece font styles (Standard, Classic, Merida, Symbola)
- Optional bold styling for symbols
- Stroke and border customization
- Sound effects for piece movements and interactions
- Smooth and realistic piece movements
- Legal move validation and turn tracking
- Play against AI (powered by Gemini)
- Multiplayer-ready structure
- Standalone game or library integration
- Easy XML or Kotlin integration
- Fully documented source code
- **Responsive Chessboard**: Adjusts to different screen sizes
- **Customizable Chess Pieces**: Personalize piece colors, styles, and strokes
- **Pawn Promotion**: Automatically promotes pawns
- **Highlight Selected Squares**: Visual cues for selections and valid moves
- **Turn Indicator**: Displays whose turn it is
- **Touch Input Support**: Select and move pieces by tapping and dragging
- **Dynamic Square Colors**: Customize light and dark square colors
- **Custom Stroke Effects**: Add customizable strokes to pieces and squares

## 📦 Installation

Add the following to your `build.gradle` (project-level):

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Then add the dependency in your app-level `build.gradle`:

```gradle
implementation 'com.github.Jumman04:Jummania-Chess-Game:1.0'
```

---

## 🧩 Usage

### In XML:

```xml

<com.jummania.ChessView
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:chess="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/chessView"

        chess:lightSquareColor="#fadeaf"
        chess:darkSquareColor="#8e4f19"
        chess:isDarkFilled="true"
        chess:isLightFilled="true"
        chess:enableStroke="true"
        chess:pieceDarkColor="#000"
        chess:pieceLightColor="#fff"
        chess:strokeLightColor="#000"
        chess:strokeDarkColor="#000"
        chess:symbolStyle="symbola" />
```

### Programmatically:

```kotlin
chessView.setSoundEffectEnabled(true)
chessView.setBackgroundColor(Color.WHITE)
chessView.setPieceStyle(true, true, Color.WHITE, Color.BLACK)
chessView.setSquareColors("#fadeaf".toColorInt(), "#8e4f19".toColorInt())
chessView.setEnableStroke(true, Color.BLACK, Color.BLACK)
chessView.setSymbolStyle(SymbolStyle.SYMBOLA.ordinal, useBoldSymbol = false)
```

---

## Customization Options

The game offers extensive customization, allowing you to tailor the appearance and behavior of the
chessboard and pieces:

### Chessboard Colors:

- **Light Square Color** (`lightSquareColor`)
- **Dark Square Color** (`darkSquareColor`)

### Piece Colors:

- **Light Piece Color** (`pieceLightColor`)
- **Dark Piece Color** (`pieceDarkColor`)

### Stroke Effects:

- **Enable Stroke** (`enableStroke`)
- **Stroke Color for Light Pieces** (`strokeLightColor`)
- **Stroke Color for Dark Pieces** (`strokeDarkColor`)

### Font Style for Piece Symbols:

- **Symbol Style** (`symbolStyle`)
    - `standard`: Default system font.
    - `classic`: `chess_alpha.ttf` (Classic chess font).
    - `merida`: `chess_merida_unicode.ttf` (Merida chess font).
    - `symbola`: `symbola.ttf` (Unicode chess symbols).

### Sound Effects:

- **Enable Sound Effects** (`enableSoundEffect`): Enable or disable sound effects for piece
  movement.

## Technologies Used

- **Kotlin**: The primary language for Android development.
- **Android Studio**: Integrated development environment used to build and test the game.
- **Custom Views**: Custom `ChessView` class handles drawing the chessboard, pieces, and other
  visual elements.
- **Sound Effects**: Integrated system sound effects for piece movements and interactions.
- **Material Design**: Material design principles are used for clean and consistent UI elements.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.