/*
Name: Andy Masuda
Email: andyxmasuda@gmail.com
Date: 12/9/2021
Project Name: Final Project - Minesweeper
Course: CS 17.11
Description: This class represents a single tile on the board.
 */
package edu.srjc.masuda.andy.Minesweeper;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Tile extends StackPane
{
    private int row;
    private int col;
    private boolean hasMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private boolean isEmpty;
    private final Text text = new Text();
    private final Rectangle border = new Rectangle(UIController.getTileSize() - 1, UIController.getTileSize() - 1);
    private final Color[] colors = {Color.BLUE, Color.GREEN, Color.RED, Color.PURPLE, Color.FIREBRICK, Color.TURQUOISE, Color.BLACK, Color.GRAY};

    public Tile(int row, int col, boolean hasMine)
    {
        setRow(row);
        setCol(col);
        setHasMine(hasMine);
        setRevealed(false);
        setFlagged(false);
        setEmpty(true);
        if (hasMine)
        {
            setEmpty(false);
        }
        border.setStroke(Color.web("#90e0ef"));
        text.setVisible(false);
        setTranslateX(col * UIController.getTileSize());
        setTranslateY(row * UIController.getTileSize());
        getChildren().addAll(border, text);
    }

    public int getRow()
    {
        return row;
    }

    public void setRow(int row)
    {
        this.row = row;
    }

    public int getCol()
    {
        return col;
    }

    public void setCol(int col)
    {
        this.col = col;
    }

    public boolean hasMine()
    {
        return hasMine;
    }

    public void setHasMine(boolean hasMine)
    {
        this.hasMine = hasMine;
    }

    public Text getText()
    {
        return text;
    }

    public void setText(String text)
    {
        for (int i = 1; i <= colors.length; i++)
        {
            if (Integer.parseInt(text) == i)
            {
                this.text.setStroke(colors[i - 1]);
            }
        }
        this.text.setText(text);
    }

    public boolean isRevealed()
    {
        return isRevealed;
    }

    public void setRevealed(boolean open)
    {
        this.isRevealed = open;
    }

    public boolean isFlagged()
    {
        return isFlagged;
    }

    public void setFlagged(boolean flagged)
    {
        if (flagged)
        {
            border.setFill(Color.web("#023e8a"));
        }
        else
        {
            border.setFill(Color.web("#0096c7"));
        }
        this.isFlagged = flagged;
    }

    public boolean isEmpty()
    {
        return isEmpty;
    }

    public void setEmpty(boolean empty)
    {
        isEmpty = empty;
    }

    public void setTileFill(Paint color, double opacity)
    {
        border.setFill(color);
        border.setOpacity(opacity);
    }

    public void openTile()
    {
        if(!isRevealed)
        {
            isRevealed = true;
            text.setVisible(true);
            border.setFill(null);
            UIController.unOpenedTileCount--;

            if (hasMine())
            {
                setTileFill(Color.RED, 1);
                UIController.gameOver = true;
                return;
            }
            if (getText().getText().isEmpty())
            {
                for (Tile tile : UIController.getNeighbors(this))
                {
                    tile.openTile();
                }
            }
        }
    }
}
