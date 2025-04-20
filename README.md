<p align="center">
   <img src="https://github.com/user-attachments/assets/b17218f0-d731-4c77-afb0-90ae43cec929" alt="App Icon" width="666">
   <br>
   <img src="https://img.shields.io/badge/API-17%2B-brightgreen.svg?style=flat" alt="API Level 17 and above"/>
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
   <img src="https://img.shields.io/github/license/Jumman04/Jummania-Chess-Game" alt="GitHub License"/>
   </a>
</p>

# Chess Game for Android

A fully-featured Chess Game for Android, built with Kotlin and Android Studio, offering smooth interactions, realistic piece movements, customizable sound effects, and various themes for chessboard squares and pieces. It supports multiple symbol styles and stroke options for a personalized experience.

This game can also be used as a library, allowing developers to integrate chess gameplay into their own Android apps by importing it as a dependency.

## Features

- **Responsive Chessboard**: The game dynamically adjusts to different screen sizes, ensuring a consistent experience across devices.
- **Customizable Chess Pieces**: Choose from a variety of customizable chess piece colors, styles, and strokes for a personalized game.
- **Sound Effects**: Enable or disable sound effects during piece movements and interactions for a more immersive experience.
- **Pawn Promotion**: The game automatically handles pawn promotion when a pawn reaches the opposite side of the board.
- **Highlight Selected Squares**: Squares and pieces are highlighted to indicate selections and valid moves.
- **Turn Indicator**: A visual indicator at the top of the board shows whose turn it is, with options for both players (e.g., Player 1, Player 2).
- **Piece Symbol Customization**: Choose from different fonts and styles for displaying chess piece symbols.
- **Touch Input Support**: Select and move pieces by simply tapping and dragging on the screen.
- **Dynamic Square Colors**: Customize the colors of the dark and light squares on the board.
- **Custom Stroke Effects**: Add stroke effects around selected pieces and squares with customizable stroke colors.

## Customization Options

The game offers extensive customization, allowing you to tailor the appearance and behavior of the chessboard and pieces:

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
- **Enable Sound Effects** (`enableSoundEffect`): Enable or disable sound effects for piece movement.

## Board Layout & Interaction

- **Piece Movement**: Select a piece by tapping on it. Then tap on a valid square to move the piece. The piece will highlight the valid squares it can move to.
- **Selected Square Highlight**: When a square is selected, it will be highlighted with a red stroke to indicate the current selection.
- **Touch Events**: Touch events are handled to enable intuitive and responsive piece selection and movement. The app determines the square under the user's touch and manages selection, deselection, and movement of pieces.

## Board Display

- **Draw Board**: The chessboard is drawn using a custom `ChessView` class, which dynamically adjusts the layout of the chess pieces and squares based on screen size.
- **Turn Indicator**: The current player's turn is visually displayed at the top of the screen, changing based on whose turn it is (White or Black).

## Technologies Used

- **Kotlin**: The primary language for Android development.
- **Android Studio**: Integrated development environment used to build and test the game.
- **Custom Views**: Custom `ChessView` class handles drawing the chessboard, pieces, and other visual elements.
- **Sound Effects**: Integrated system sound effects for piece movements and interactions.
- **Material Design**: Material design principles are used for clean and consistent UI elements.

## Setup & Installation

1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/your-username/chess-game.git
