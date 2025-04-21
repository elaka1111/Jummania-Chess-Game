package com.jummania

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import com.jummania.ChessView.Companion.isWhiteTurn


/**
 * ChessView class represents a custom view for rendering a chessboard and handling user interactions
 * in a chess game. It manages the board's visual appearance, including the drawing of squares, pieces,
 * selection indicators, and other UI elements like player names and the turn indicator.
 *
 * The class is designed to handle the entire graphical representation of the chess game, drawing the
 * board, pieces, and UI elements, as well as managing touch events to select and move pieces on the board.
 * It includes logic for handling pawn promotion, piece selection, and turn management.
 *
 * Key Features:
 * - Drawing the chessboard with alternating dark and light squares.
 * - Rendering chess pieces with their corresponding symbols on the squares.
 * - Handling user touch events to select and move pieces.
 * - Highlighting the selected square and providing feedback for pawn promotion.
 * - Displaying additional UI elements like player labels and a turn indicator.
 *
 * @property enableStroke A boolean flag to enable or disable the stroke effect for chess pieces.
 * @property isWhiteTurn A boolean flag to indicate if it's white's turn.
 * @property isSelected A boolean flag to indicate if a square or piece is selected.
 * @property selectedRowNumber The index of the currently selected row or piece.
 * @property touchedX The X-coordinate of the user's touch.
 * @property touchedY The Y-coordinate of the user's touch.
 * @property isInvalidate A flag to track if the view needs to be invalidated for redrawing.
 * @property chessController An instance of the ChessController to manage game logic and piece movements.
 * @property bitmap A bitmap representing the pawn promotion image (if applicable).
 * @property strokeLightColor The color for the stroke effect on light pieces.
 * @property strokeDarkColor The color for the stroke effect on dark pieces.
 * @property lightSquareColor The color for the light squares on the chessboard.
 * @property darkSquareColor The color for the dark squares on the chessboard.
 * @property symbolPaint A Paint object used for drawing the chess piece symbols.
 * @property enableSoundEffect A boolean flag to enable or disable sound effects on actions.
 *
 * Created by Jummania on 24/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */

@Keep
class ChessView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint object for general drawing tasks such as drawing squares, borders, etc.
    private val paint = Paint()

    // Paint object specifically for drawing chess piece symbols (e.g., ♙, ♟, etc.)
    private val symbolPaint = Paint()

    // Boolean flag to track if a piece or square is selected. Affects how the view is rendered.
    private var isSelected: Boolean = false

    // Boolean flag that indicates if the view should be redrawn. Often set to true when the game state changes.
    private var isInvalidate: Boolean = false

    // Boolean flag to enable/disable stroke effects (e.g., borders or highlights) around selected squares or pieces.
    private var enableStroke: Boolean = true

    // Boolean flag to control whether sound effects are played during actions like moves or captures.
    private var enableSoundEffect: Boolean = true

    /**
     * Bitmap used to represent a transform icon or UI element on the board.
     * It is lazily initialized to improve performance and avoid unnecessary memory usage
     * until it’s actually needed.
     */
    private val bitmap by lazy { getBitmapFromVectorDrawable(context, R.drawable.transform) }

    /**
     * Main controller that handles chess logic, move validation, castling, and other gameplay rules.
     * This is initialized externally and used to delegate all chess-related processing.
     */
    private lateinit var chessController: ChessController

    /**
     * Stores the X coordinate of the last touch event. Used to determine which square was tapped.
     */
    private var touchedX: Float = 0f

    /**
     * Stores the Y coordinate of the last touch event. Used alongside [touchedX] to determine square selection.
     */
    private var touchedY: Float = 0f

    /**
     * Index of the currently selected row on the chessboard, or -1 if no selection is active.
     */
    private var selectedRowNumber = -1

    /**
     * Color used to draw the dark squares on the chessboard.
     * Default is a warm brown tone (#8e4f19).
     */
    private var darkSquareColor: Int = "#8e4f19".toColorInt()

    /**
     * Color used to draw the light squares on the chessboard.
     * Default is a soft cream tone (#fadeaf).
     */
    private var lightSquareColor: Int = "#fadeaf".toColorInt()

    /**
     * Color of the stroke (border) for pieces or squares on light backgrounds.
     * Default is white for contrast on light tiles.
     */
    private var strokeLightColor: Int = Color.WHITE

    /**
     * Color of the stroke (border) for pieces or squares on dark backgrounds.
     * Default is black for contrast on dark tiles.
     */
    private var strokeDarkColor: Int = Color.BLACK

    companion object {
        /**
         * Tracks the current turn in the game.
         *
         * `true` indicates it is White's turn to move, `false` indicates it is Black's turn.
         * This value is shared across all instances of the class to maintain consistent game state.
         */
        var isWhiteTurn = true
    }


    /**
     * Initializes the ChessView with custom XML attributes or default values.
     *
     * This block reads the XML-defined attributes from `res/values/attrs.xml` and applies them
     * using the corresponding setter functions to configure the view's appearance and behavior.
     * It includes piece styles, square colors, symbol font, stroke customization, and sound settings.
     *
     * The following attributes are handled:
     * - `isLightFilled` and `isDarkFilled`: Determines if white and black pieces are filled.
     * - `pieceLightColor` and `pieceDarkColor`: Color of white and black pieces.
     * - `lightSquareColor` and `darkSquareColor`: Background colors of the chessboard squares.
     * - `symbolStyle`: Font style used to draw piece symbols.
     * - `useBoldSymbol`: Whether the symbols are bold.
     * - `enableStroke`: Enables outlines around the pieces.
     * - `strokeLightColor` and `strokeDarkColor`: Stroke colors for white and black pieces.
     * - `enableSoundEffect`: Enables sound effects on move actions.
     *
     * Also sets up the default paint configuration and the background color.
     */
    init {
        // Obtain the attributes from the XML
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ChessView, defStyleAttr, defStyleAttr
        )

        try {
            // Set piece styles: light and dark pieces color filling
            setPieceStyle(
                typedArray.getBoolean(R.styleable.ChessView_isLightFilled, true),
                typedArray.getBoolean(R.styleable.ChessView_isDarkFilled, true),
                typedArray.getColor(R.styleable.ChessView_pieceLightColor, Color.WHITE),
                typedArray.getColor(R.styleable.ChessView_pieceDarkColor, Color.BLACK)
            )

            // Set square colors for light and dark squares
            setSquareColors(
                typedArray.getColor(R.styleable.ChessView_lightSquareColor, lightSquareColor),
                typedArray.getColor(R.styleable.ChessView_darkSquareColor, darkSquareColor)
            )

            // Set the symbol font style, with an option for bold symbols
            setSymbolStyle(
                SymbolStyle.fromInt(
                    typedArray.getInt(
                        R.styleable.ChessView_symbolStyle, 1 // Default style is "CLASSIC"
                    )
                ), typedArray.getBoolean(R.styleable.ChessView_useBoldSymbol, false)
            )

            // Enable/disable stroke for pieces, set stroke colors
            setEnableStroke(
                typedArray.getBoolean(R.styleable.ChessView_enableStroke, enableStroke),
                typedArray.getColor(
                    R.styleable.ChessView_strokeLightColor, chessController.pieceDarkColor
                ),
                typedArray.getColor(
                    R.styleable.ChessView_strokeDarkColor, chessController.pieceDarkColor
                )
            )

            // Enable or disable sound effects for piece movement
            setSoundEffectEnabled(
                typedArray.getBoolean(
                    R.styleable.ChessView_enableSoundEffect, true
                )
            )
        } finally {
            // Always recycle the typedArray to free up memory
            typedArray.recycle()
        }

        // Set up paint styles for symbol rendering
        symbolPaint.style = Paint.Style.FILL // Fill the symbols
        symbolPaint.textAlign = Paint.Align.CENTER // Align text to center for symbols
        paint.textAlign = Paint.Align.CENTER // Align text for other painting actions
        symbolPaint.isAntiAlias = true // Enable anti-aliasing for smoother symbols

        // Set the background color for the chessboard (default: white if undefined)
        setBackgroundColor((background as? ColorDrawable)?.color ?: Color.WHITE)
    }


    /**
     * Handles the drawing of the chessboard, pieces, selection, and other visual elements.
     *
     * This function is responsible for drawing the entire chessboard, including:
     * - Drawing the squares on the board (alternating dark and light).
     * - Highlighting the selected square.
     * - Drawing the chess pieces on the board.
     * - Drawing additional UI elements like player labels, the turn indicator, and board borders.
     *
     * It also handles touch events to detect which square was touched and potentially select a piece or perform a move.
     *
     * @param canvas The canvas on which the chessboard and all visual elements are drawn.
     */
    override fun onDraw(canvas: Canvas) {

        // Calculate the minimum size of the board to maintain a square shape, with padding
        val minSize = minOf(height, width) - 55
        val boardLeft = (width - minSize) / 2f  // Horizontal starting point for the board
        val boardTop = (height - minSize) / 2f  // Vertical starting point for the board
        val squareSize = minSize / 8f  // Size of each square on the chessboard

        var rowIndex = -1  // Initialize the row index
        var touchedInside = false  // Flag to check if the touch is inside a square

        // Iterate over each row and column of the chessboard to draw the squares and pieces
        for (row in 0 until 8) {
            val top = boardTop + row * squareSize  // Top coordinate of the current row
            val bottom = top + squareSize  // Bottom coordinate of the current row
            var isDarkSquare = row % 2 != 0  // Alternate square colors for each row

            for (col in 0 until 8) {
                val left = boardLeft + col * squareSize  // Left coordinate of the current column
                val right = left + squareSize  // Right coordinate of the current column

                rowIndex++

                // Check if the touch event is within the bounds of the current square
                if (touchedX in left..right && touchedY in top..bottom) {
                    touchedInside = handleTouch(rowIndex)  // Handle the touch (selection or move)
                }

                // Draw the current square with the appropriate color
                drawSquare(canvas, left, top, right, bottom, isDarkSquare)

                // Highlight the selected square if this is the one selected by the user
                if (selectedRowNumber == rowIndex && touchedInside && isSelected) {
                    drawSelection(canvas, left, top, right, bottom, squareSize)
                }

                // Draw the chess piece in the current square, if one exists
                val piece = chessController.get(rowIndex)
                if (piece != null) {
                    drawSymbol(
                        canvas, squareSize * 0.6f,  // Symbol size as a percentage of square size
                        piece,  // The piece to be drawn
                        left + squareSize / 2,  // Horizontal center of the square
                        top + squareSize / 1.5f  // Vertical position (slightly lower than the center)
                    )
                }

                // Alternate the color of the squares for the next column
                isDarkSquare = !isDarkSquare
            }
        }

        // If no square was touched, deselect any currently selected piece
        if (!touchedInside) isSelected = false

        // Draw the border around the entire board
        drawBoardBorder(canvas, boardLeft, boardTop, minSize)

        // Draw the player labels
        drawText(canvas, boardTop, squareSize, "Player 1")
        drawText(canvas, boardTop + minSize * 1.3f, squareSize, "Player 2")

        // Draw the turn indicator to show whose turn it is
        drawTurnIndicator(canvas, boardTop, minSize, squareSize)
    }


    /**
     * Handles the touch event on a chess piece row.
     *
     * This function is responsible for managing the selection and movement of a chess piece when a row is touched.
     * It supports the following behaviors:
     * - Deselecting the piece if it's already selected.
     * - Swapping the chess piece to a different row if it's selected and a valid move is made.
     * - Selecting a chess piece if it's not already selected and no invalidation is pending.
     *
     * @param rowNumber The row number that was touched, used to determine the piece selection and movement.
     * @return A boolean indicating whether the touch was handled. `true` means the touch was successfully handled,
     *         and `false` means it was not (e.g., due to invalidation).
     */
    private fun handleTouch(rowNumber: Int): Boolean {
        return when {
            // Case 1: The same row was selected, and the piece is already selected
            selectedRowNumber == rowNumber && isSelected -> {
                // Deselect the piece
                isSelected = false
                selectedRowNumber = -1
                true
            }

            // Case 2: A piece is selected and the move is valid (the piece can be swapped)
            isSelected && swapTo(selectedRowNumber, rowNumber, false) -> true


            // Case 3: No invalidation is pending and no piece is selected
            !isInvalidate -> {
                // Select the new piece on the specified row
                isSelected = true
                selectedRowNumber = rowNumber
                true
            }

            // Default case: Touch is ignored if invalidation is pending
            else -> false
        }
    }


    /**
     * Draws a square on the chessboard.
     *
     * This method draws a single square on the chessboard with a color that depends on whether
     * the square is dark or light. The color is determined by the `isDark` parameter, with a
     * specific color assigned to dark squares and another to light squares.
     *
     * @param canvas The canvas on which the square will be drawn.
     * @param left The horizontal position of the left edge of the square.
     * @param top The vertical position of the top edge of the square.
     * @param right The horizontal position of the right edge of the square.
     * @param bottom The vertical position of the bottom edge of the square.
     * @param isDark A boolean flag indicating whether the square is dark or light.
     */
    private fun drawSquare(
        canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, isDark: Boolean
    ) {
        // Set the paint style to FILL, meaning the square will be filled with a solid color
        paint.style = Paint.Style.FILL

        // Choose the color based on whether the square is dark or light
        paint.color = if (isDark) darkSquareColor else lightSquareColor

        // Draw the square on the canvas
        canvas.drawRect(left, top, right, bottom, paint)
    }


    /**
     * Draws a selection border around a chessboard square.
     *
     * This method draws a red rectangular border around a square on the chessboard to visually
     * indicate the currently selected square. The selection border is drawn with a small padding
     * around the edges, determined by the size of the square, for a clean and subtle effect.
     *
     * @param canvas The canvas on which the selection border will be drawn.
     * @param left The horizontal position of the left edge of the square.
     * @param top The vertical position of the top edge of the square.
     * @param right The horizontal position of the right edge of the square.
     * @param bottom The vertical position of the bottom edge of the square.
     * @param size The size of the square. It is used to determine the padding for the selection border.
     */
    private fun drawSelection(
        canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, size: Float
    ) {
        // Set the paint style to STROKE, meaning only the outline (border) will be drawn
        paint.style = Paint.Style.STROKE

        // Calculate padding as 5% of the square's size for a subtle effect
        val padding = size * 0.05f

        // Set the stroke width to the calculated padding to create a thinner border
        paint.strokeWidth = padding

        // Set the paint color to red for the selection border
        paint.color = Color.RED

        // Draw the rectangle with the specified padding around the edges
        canvas.drawRect(left + padding, top + padding, right - padding, bottom - padding, paint)
    }


    /**
     * Draws the border of the chessboard square at the specified position.
     *
     * This method draws a rectangular border on the canvas representing the chessboard square
     * at the given `x` and `y` coordinates. The border is drawn with a black color and a
     * stroke width of 5 pixels, providing a clear visual separation between squares.
     *
     * @param canvas The canvas on which the board border will be drawn.
     * @param x The horizontal position (left edge) of the square's border.
     * @param y The vertical position (top edge) of the square's border.
     * @param min The width and height of the square. This defines the size of the square
     *            and is used for both the horizontal and vertical dimensions of the rectangle.
     */
    private fun drawBoardBorder(canvas: Canvas, x: Float, y: Float, min: Int) {
        // Set the paint color to black for the border
        paint.color = Color.BLACK

        // Set the paint style to STROKE, so only the border (outline) will be drawn
        paint.style = Paint.Style.STROKE

        // Set the stroke width to 5 pixels for a thick border
        paint.strokeWidth = 5f

        // Draw the rectangular border on the canvas at the specified position
        canvas.drawRect(x, y, x + min, y + min, paint)
    }


    /**
     * Draws the given text on the chessboard at a specified vertical position.
     *
     * This method is responsible for drawing text on the canvas at a given `y` position. The
     * text will be horizontally centered at the middle of the canvas (`width / 2f`). The size
     * of the text is determined by the provided `size` parameter, and the text will be drawn
     * in black color.
     *
     * @param canvas The canvas to draw the text on.
     * @param y The vertical position where the text will be drawn.
     * @param size The size of the text, used to adjust its appearance.
     * @param text The text string to be drawn.
     */
    private fun drawText(canvas: Canvas, y: Float, size: Float, text: String) {
        // Set the text size based on the provided size parameter
        paint.textSize = size / 2f

        // Set the paint color to black for the text
        paint.color = Color.BLACK

        // Set the paint style to FILL to fill the text with color
        paint.style = Paint.Style.FILL

        // Draw the text at the center horizontally and at the specified vertical position
        canvas.drawText(text, width / 2f, y - size, paint)
    }


    /**
     * Draws an indicator to show whose turn it is on the chessboard.
     *
     * This method draws either a pawn promotion indicator or a simple circle to represent the
     * current player's turn. If the pawn can be promoted, the method will display a bitmap
     * representing the promotion area; otherwise, it will draw a circle. The indicator's position
     * depends on the current player's turn and the provided vertical position `y`.
     *
     * The indicator is drawn at the center of the board horizontally and either above or below
     * the provided `y` value depending on whether it is White's turn or Black's turn.
     *
     * @param canvas The canvas to draw on.
     * @param y The vertical position for the indicator.
     * @param min The minimum value used to adjust the position of the indicator.
     * @param size The size of the indicator, used to calculate the exact position.
     */
    private fun drawTurnIndicator(canvas: Canvas, y: Float, min: Int, size: Float) {
        // Determine the vertical position of the indicator based on the current turn
        val indicatorY = if (isWhiteTurn) y - size / 2 else y + min + size / 2
        val centerX = width / 2f
        val topY = indicatorY - size / 4

        // Set the paint color to red for the turn indicator
        paint.color = Color.RED

        // Check if the pawn can be revived (promoted) and if the bitmap for promotion is available
        if (chessController.pawnCanRevive(selectedRowNumber) && bitmap != null) {
            // Draw the bitmap (promotion indicator) at the calculated position
            canvas.drawBitmap(bitmap!!, centerX, topY, paint)

            // Check if the user tapped in the promotion area and trigger the promotion logic
            checkPawnPromotion(centerX, topY)
        } else {
            // Draw a simple circle as the turn indicator if no promotion is available
            canvas.drawCircle(centerX, indicatorY, 22f, paint)
        }
    }


    /**
     * Checks if a pawn has reached the promotion square and triggers pawn promotion if necessary.
     *
     * This method checks if the touch event (based on the coordinates `touchedX` and `touchedY`)
     * falls within the bounds of the promotion area for the pawn. If the touch is inside the promotion
     * area, the `revivePawn()` method of the `chessController` is called to initiate the pawn promotion process.
     *
     * The promotion area is defined by a bitmap (representing the promotion icon or area), and
     * the touch coordinates (`centerX`, `topY`) are used to determine if the user clicked on the
     * promotion region.
     *
     * @param centerX The horizontal center position of the promotion area.
     * @param topY The vertical top position of the promotion area.
     */
    private fun checkPawnPromotion(
        centerX: Float, topY: Float
    ) {
        // Check if the bitmap for the promotion area is not null and if the touch coordinates
        // are inside the bounds of the promotion area.
        if (bitmap != null && touchedX in centerX..centerX + bitmap!!.width && touchedY in topY..topY + bitmap!!.height) {
            // Call the chessController's revivePawn method to promote the pawn.
            chessController.revivePawn(selectedRowNumber)
        }
    }


    /**
     * Draws the symbol of a chess piece on the canvas.
     *
     * This function is responsible for drawing the symbol of a chess piece, considering factors like stroke,
     * light or dark pieces, and whether the piece is filled. The drawing is done with transformations if necessary.
     *
     * @param canvas The canvas on which the symbol will be drawn.
     * @param textSize The size of the text used to render the chess piece symbol.
     * @param piece The chess piece whose symbol is to be drawn.
     * @param x The x-coordinate of the position where the symbol should be drawn.
     * @param y The y-coordinate of the position where the symbol should be drawn.
     */
    private fun drawSymbol(
        canvas: Canvas, textSize: Float, piece: Piece, x: Float, y: Float
    ) {
        // Set the text size for drawing the symbol
        symbolPaint.textSize = textSize

        // Get the symbol for the piece
        val symbol = piece.symbol
        val isLightPiece = chessController.isLightPiece(piece)

        // If stroke effect is enabled, handle drawing with a background layer
        if (enableStroke) {
            // Transform the symbol if necessary (e.g., mirroring or flipping)
            val transformedSymbol = chessController.transform(symbol)

            // Determine whether to draw a background behind the symbol based on the piece's color
            val shouldDrawBackground = if (isLightPiece) {
                !chessController.isLightFilled
            } else {
                !chessController.isDarkFilled
            }

            // Draw background (stroke layer) if needed
            if (shouldDrawBackground) {
                symbolPaint.color = if (isLightPiece) strokeLightColor else strokeDarkColor
                canvas.drawText(transformedSymbol, x, y, symbolPaint)
            }

            // Draw the actual piece symbol in the appropriate color
            symbolPaint.color = piece.color
            canvas.drawText(symbol, x, y, symbolPaint)

            // Draw top layer of the symbol if the piece is filled
            val shouldDrawTopLayer = if (isLightPiece) {
                chessController.isLightFilled
            } else {
                chessController.isDarkFilled
            }

            if (shouldDrawTopLayer) {
                symbolPaint.color = if (isLightPiece) strokeLightColor else strokeDarkColor
                canvas.drawText(transformedSymbol, x, y, symbolPaint)
            }

        } else {
            // If stroke is disabled, just draw the piece symbol
            symbolPaint.color = piece.color
            canvas.drawText(symbol, x, y, symbolPaint)
        }
    }


    /**
     * Converts a vector drawable resource to a [Bitmap].
     *
     * This method fetches the drawable using the provided [drawableId] and converts it
     * to a bitmap using the [toBitmap] extension.
     *
     * @param context The context used to access resources.
     * @param drawableId The resource ID of the vector drawable.
     * @return A [Bitmap] representation of the vector drawable, or `null` if drawable not found.
     */
    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        return AppCompatResources.getDrawable(context, drawableId)?.toBitmap()
    }


    /**
     * Sets the colors for the light and dark squares on the chessboard.
     *
     * @param light The color to be used for the light squares (default is current [lightSquareColor]).
     * @param dark  The color to be used for the dark squares (default is current [darkSquareColor]).
     */
    fun setSquareColors(light: Int = lightSquareColor, dark: Int = darkSquareColor) {
        lightSquareColor = light
        darkSquareColor = dark
    }


    /**
     * Initializes the [ChessController] with the given piece styles and colors.
     *
     * @param isLightFilled  Determines whether light-side pieces are filled or outlined.
     * @param isDarkFilled   Determines whether dark-side pieces are filled or outlined.
     * @param lightColor     The color to use for the light-side pieces.
     * @param darkColor      The color to use for the dark-side pieces.
     *
     * Also sets a revival callback to reset the selection state and redraw the board
     * whenever a pawn is promoted (revived).
     */
    fun setPieceStyle(
        isLightFilled: Boolean,
        isDarkFilled: Boolean,
        lightColor: Int,
        darkColor: Int,
    ) {
        // Create a new ChessController with the specified styles and colors.
        chessController =
            ChessController(context, isLightFilled, isDarkFilled, lightColor, darkColor)

        // Register a callback that triggers when a pawn is revived (promoted),
        // resetting touch state and requesting a view redraw.
        chessController.setAfterRevival {
            touchedX = 0f
            touchedY = 0f
            selectedRowNumber = -1
            isInvalidate = true
            invalidate()
        }
    }


    /**
     * Sets the typeface and bold style used for rendering chess piece symbols.
     *
     * This function allows customization of the chess symbol font style and weight.
     * It uses predefined [SymbolStyle] options and an optional bold setting.
     *
     * @param style The desired [SymbolStyle] to use for chess piece symbols.
     * @param useBoldSymbol `true` to apply bold style to the symbols, `false` for normal weight.
     */
    fun setSymbolStyle(style: SymbolStyle, useBoldSymbol: Boolean) {
        fun setFont(id: Int): Typeface? {
            return ResourcesCompat.getFont(context, id)
        }

        val typeface = when (style) {
            SymbolStyle.CLASSIC -> setFont(R.font.chess_alpha)
            SymbolStyle.MERIDA -> setFont(R.font.chess_merida_unicode)
            SymbolStyle.SYMBOLA -> setFont(R.font.symbola)
            SymbolStyle.STANDARD -> null
        }

        symbolPaint.typeface =
            Typeface.create(typeface, if (useBoldSymbol) Typeface.BOLD else Typeface.NORMAL)
    }


    /**
     * Enables or disables stroke (border) around chessboard squares and sets stroke colors.
     *
     * This function allows customization of the stroke visibility and its colors for light and dark squares.
     *
     * @param enable `true` to enable square stroke, `false` to disable it.
     * @param lightColor The stroke color for light squares.
     * @param darkColor The stroke color for dark squares.
     */
    fun setEnableStroke(enable: Boolean, lightColor: Int, darkColor: Int) {
        enableStroke = enable
        strokeLightColor = lightColor
        strokeDarkColor = darkColor
    }


    /**
     * Enables or disables sound effects during gameplay.
     *
     * When enabled, actions like piece movement or captures may trigger sound effects.
     *
     * @param enable `true` to enable sound effects, `false` to disable them.
     */
    fun setSoundEffectEnabled(enable: Boolean) {
        enableSoundEffect = enable
    }


    /**
     * Handles the click event for the view.
     *
     * This method is triggered when the view is clicked. It performs the following actions:
     * 1. If sound effects are enabled (`enableSoundEffect` is `true`), it plays a click sound.
     * 2. Marks the view as needing to be redrawn by setting `isInvalidate` to `false` and triggering a call to `invalidate()`.
     * 3. Calls the superclass implementation of `performClick()` to ensure any default click behavior is executed.
     *
     * The main behavior of this method is to handle the click event by possibly playing a sound effect
     * and initiating a redraw of the view (if necessary).
     *
     * @return `true` if the click was handled, `false` otherwise.
     */
    override fun performClick(): Boolean {
        // Check if sound effects are enabled
        if (enableSoundEffect) {
            // Play the click sound effect
            playSoundEffect(SoundEffectConstants.CLICK)
        }

        // Set the invalidate flag to false (to control redrawing behavior)
        isInvalidate = false

        // Invalidate the view to trigger a redraw (if required)
        invalidate()

        // Call the super method to handle any additional default behavior
        return super.performClick()
    }


    /**
     * Handles touch events for the chessboard view.
     *
     * This method listens for touch events, specifically the action when a touch is initiated (`ACTION_DOWN`).
     * When a touch event is detected, it captures the X and Y coordinates of the touch location,
     * and triggers a click event. This method returns `true` if the touch event was handled.
     *
     * The main behavior occurs when the user taps on the view, and the touch coordinates are captured
     * for further actions (such as piece movement or selection).
     *
     * @param event The motion event associated with the touch gesture. This contains information like the action type and the X and Y coordinates of the touch.
     * @return `true` if the touch event is handled, `false` if it is passed to the super class for further handling.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Check if the action is a touch down event
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (chessController.fromOnline()) {
                Toast.makeText(context, "ai turn", Toast.LENGTH_SHORT).show()
                return true
            }
            // Capture the X and Y coordinates of the touch
            touchedX = event.x
            touchedY = event.y

            // Trigger the click event (useful for accessibility or custom click actions)
            performClick()

            // Return true to indicate the event has been handled
            return true
        }

        // If the event is not a touch down, pass it to the super class for further processing
        return super.onTouchEvent(event)
    }

    fun withOnlinePlayer(function: (friends: String, enemies: String) -> Unit) {
        chessController.setOnlinePlayer(function)
    }

    fun swapTo(fromIndex: Int, toIndex: Int, isOnline: Boolean): Boolean {
        val isSwapped = chessController.swapTo(fromIndex, toIndex, isOnline)
        if (isSwapped) {
            isSelected = false
            selectedRowNumber = -1
            isInvalidate = true
            invalidate()
        } else if (isOnline) {
            chessController.sendData()
        }
        return isSwapped
    }

}