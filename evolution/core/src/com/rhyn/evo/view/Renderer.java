package com.rhyn.evo.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.rhyn.evo.GameConstants;
import com.rhyn.evo.GameManager;
import com.rhyn.evo.control.Controller;
import com.rhyn.evo.model.Bot;
import com.rhyn.evo.model.GameObject;
import com.rhyn.evo.model.World;
import com.rhyn.evo.utils.InputManager;

public class Renderer implements UI_Constants, GameConstants{
	
	private SpriteBatch batch;
	private ShapeRenderer shr;
	private FPSLogger fps = new FPSLogger();
	
	private BitmapFont fontClassic;
	
	private GameObject glowedBot;
	private GameObject selectedBot;
	
	public static boolean showTemperatureMap = false;
	public static boolean showGrid = false;
	public static boolean showEnergy = false;
	
	public Renderer(){
		batch = new SpriteBatch();
		fontClassic = new BitmapFont();
		shr = new ShapeRenderer();
		shr.setAutoShapeType(true);
	}
	
	public void render(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		glowedBot = null;
		
		drawField();
		drawBots();
		drawUI();
		
		//fps.log();
	}
	
	public void drawField(){
		float canvasX = UI_FIELD_MARGIN_LEFT;
		float canvasY = UI_FIELD_MARGIN_BOTTOM;
		float canvasWidth = GameManager.width - (UI_FIELD_MARGIN_RIGHT + UI_FIELD_MARGIN_LEFT);
		float canvasHeight = GameManager.height - (UI_FIELD_MARGIN_TOP + UI_FIELD_MARGIN_BOTTOM);
		float fieldSize = canvasWidth > canvasHeight ? canvasHeight : canvasWidth;
		float cellSize = fieldSize / World.size;
		float x0 = canvasX + (canvasWidth - fieldSize) / 2;
		float y0 = canvasY + (canvasHeight - fieldSize) / 2;
		
		shr.begin();
		{
			if(showTemperatureMap)
				for(int i = 0; i < World.size; i++){
					shr.set(ShapeRenderer.ShapeType.Filled);
					shr.setColor(World.getLineTemperature(World.size - i) / World.season.maxTemperature, World.getLineTemperature(World.size - i) / World.season.maxTemperature - .1f, .5f-World.getLineTemperature(World.size - i) / World.season.maxTemperature, 1f);
					shr.rect(x0, y0 + fieldSize - i*cellSize, fieldSize, -cellSize);
				}
			if(showGrid){
				shr.set(ShapeRenderer.ShapeType.Line);
				if(showTemperatureMap)shr.setColor(0f, 0f, 0f, 1f);
				else shr.setColor(0.25f, 0.25f, 0.25f, 1f);
				for(int i = 0; i < World.size + 1; i++){
					shr.line(x0 + i*cellSize, y0, x0 + i*cellSize, y0 + fieldSize);
				}
				for(int i = 0; i < World.size + 1; i++){
					shr.line(x0, y0 + i*cellSize, x0 + fieldSize, y0 + i*cellSize);
				}
			}
			shr.set(ShapeRenderer.ShapeType.Line);
			shr.setColor(0.25f, 0.25f, 0.25f, 1f);
			shr.rect(x0, y0, fieldSize, fieldSize);
			shr.setColor(1f, 1f, 1f, 1f);
			shr.rect(canvasX, canvasY, canvasWidth, canvasHeight);
		}
		shr.end();
		
		if(showTemperatureMap){
			batch.begin();
			{
				fontClassic.setColor(Color.WHITE);
				for(int i = 0; i < World.size; i += 4){
						fontClassic.draw(batch, ((float)((int)(World.getLineTemperature(World.size - i) * 10))/10) + "t", x0 + fieldSize, y0 + fieldSize - i * cellSize);
					
				}
				for(int i = 0; i < World.size; i += 3){
					if(i < (int)(World.size / 2))
						continue;
					fontClassic.draw(batch, ((float)((int)(World.getLineMineral(World.size - i) * 10))/10) + "m", x0 - 40, y0 + fieldSize - i * cellSize);
				}
			}
			batch.end();
		}
	}
	
	public void drawBots(){
		float canvasX = UI_FIELD_MARGIN_LEFT;
		float canvasY = UI_FIELD_MARGIN_BOTTOM;
		float canvasWidth = GameManager.width - (UI_FIELD_MARGIN_RIGHT + UI_FIELD_MARGIN_LEFT);
		float canvasHeight = GameManager.height - (UI_FIELD_MARGIN_TOP + UI_FIELD_MARGIN_BOTTOM);
		float fieldSize = canvasWidth > canvasHeight ? canvasHeight : canvasWidth;
		float cellSize = fieldSize / World.size;
		float x0 = canvasX + (canvasWidth - fieldSize) / 2;
		float y0 = canvasY + (canvasHeight - fieldSize) / 2;
		if(InputManager.mouseLeftClicked)
			if(InputManager.mouseX > canvasX &&
			   InputManager.mouseY > canvasY &&
			   InputManager.mouseX < canvasX + canvasWidth &&
			   InputManager.mouseY < canvasY + canvasHeight)
				selectedBot = null;
		
		shr.begin();
		{
			for(int i = 0; i < World.size; i++)
				for(int j = 0; j < World.size; j++)
					if(World.cell[i][j] != null){
						cellSize = fieldSize / World.size;
						float x = x0 + i*cellSize;
						float y = y0 + j*cellSize;
						
						World.cell[i][j].setGlowed(false);
						if(InputManager.mouseX > x &&
						   InputManager.mouseY > y &&
						   InputManager.mouseX < x + cellSize &&
						   InputManager.mouseY < y + cellSize)
							World.cell[i][j].setGlowed(true);
						
						if(((Bot)World.cell[i][j]).getState() == Bot.State.DEAD){
							x++;
							y++;
							cellSize-=2;
							cellSize-=2;
						}
						if(showEnergy && ((Bot)World.cell[i][j]).getState() == Bot.State.LIVE){
							shr.setColor(new Color(1f, 1f - ((Bot)World.cell[i][j]).getEnergy() / BOT_ENERGY_CAP, 0f,  1f));
							shr.set(ShapeRenderer.ShapeType.Filled);
							shr.rect(x + 1, y + 1, cellSize - 2, cellSize - 2);
						}
						else{
							shr.setColor(((Bot)World.cell[i][j]).getColor());
							shr.set(ShapeRenderer.ShapeType.Filled);
							shr.rect(x + 1, y + 1, cellSize - 2, cellSize - 2);
							shr.set(ShapeRenderer.ShapeType.Line);
							shr.setColor((((Bot)World.cell[i][j]).getColor()).r / 1.5f, (((Bot)World.cell[i][j]).getColor()).g / 1.5f, (((Bot)World.cell[i][j]).getColor()).b / 1.5f, 1f);
							shr.rect(x + 1, y + 1, cellSize - 2, cellSize - 2);
						}
						
						if(World.cell[i][j].isGlowed()){
							shr.set(ShapeRenderer.ShapeType.Line);
							shr.setColor(COLOR_BOT_GLOWED);
							shr.rect(x, y, cellSize, cellSize);
							glowedBot = World.cell[i][j];
							if(InputManager.mouseLeftClicked)
								selectedBot = World.cell[i][j];
						}
						if(World.cell[i][j] == selectedBot){
							shr.set(ShapeRenderer.ShapeType.Filled);
							shr.setColor(COLOR_BOT_SELECTED);
							shr.rect(x + 2, y + 2, cellSize - 4, cellSize - 4);
						}
					}
		}
		shr.end();
	}
	
	public void drawUI(){
		float x = UI_PADDING;
		float y = GameManager.height - UI_PADDING;
		float height = UI_FIELD_MARGIN_TOP - UI_PADDING*2;
		float width = height*4;
		
		shr.begin();
		{
			int k = 0;
			for(int i = 0; i < BOT_GENOME_SIZE / 4; i++)
				for(int j = 0; j < BOT_GENOME_SIZE / 16; j++){
					float cellSize = width / BOT_GENOME_SIZE * 4;
					Bot bot = null;
					if(glowedBot!=null)
						bot = (Bot)glowedBot;
					if(selectedBot!=null)
						bot = (Bot)selectedBot;
					if(bot!=null){
						shr.set(ShapeRenderer.ShapeType.Filled);
						if(bot.getGenomeCurrent() == k)shr.setColor(UI_COLOR_GENOME_CURRENT);
						else{
							if(bot.getGenomeMutated() == k && bot.isMutant())shr.setColor(UI_COLOR_GENOME_MUTATED);
							else shr.setColor(UI_COLOR_GENOME);
						}
						shr.rect(x + i*cellSize, y - j*cellSize, cellSize, -cellSize);
					}
					shr.set(ShapeRenderer.ShapeType.Line);
					shr.setColor(UI_COLOR_FRAME_SECONDARY);
					shr.rect(x + i*cellSize, y - j*cellSize, cellSize, -cellSize);
					
					k++;
				}
			shr.setColor(UI_COLOR_FRAME);
			shr.rect(x, y, width, -height);
		}
		shr.end();
		
		Bot bot = null;
		if(glowedBot!=null)
			bot = (Bot)glowedBot;
		if(selectedBot!=null)
			bot = (Bot)selectedBot;
		if(bot!=null){
			batch.begin();
			{
				int k = 0;
				for(int i = 0; i < BOT_GENOME_SIZE / 16; i++)
					for(int j = 0; j < BOT_GENOME_SIZE / 4; j++){
						float cellSize = width / BOT_GENOME_SIZE * 4;
						fontClassic.setColor(UI_COLOR_GENOME_TEXT);
						if(bot.getGenomeMutated() == k && bot.isMutant())fontClassic.setColor(UI_COLOR_GENOME_MUTATED_TEXT);
						fontClassic.draw(batch, bot.getGenome()[k] + "", x + j*cellSize, y - i*cellSize - 3);
						
						k++;
					}
			}
			batch.end();
		}
		
		x += width + UI_PADDING;
		
		width = GameManager.width - x*2;
		height /= 2;
		batch.begin();
		{
			fontClassic.setColor(Color.WHITE);
			fontClassic.draw(batch, World.season.toString(), x + 50, y - 3);
			fontClassic.draw(batch, "Day: "+(int)(World.getSeasonDay() + 1), x + 95, y - 20);
			fontClassic.draw(batch, "Year: "+World.year, x + 15, y - 20);
			fontClassic.draw(batch, "Population: "+ Controller.population, x, y - height - 5);
			fontClassic.draw(batch, "Hrb: "+ Controller.herbivoreCount + "%, Crn: " + Controller.carnivoreCount + "%, Mnr: " + Controller.mineralCount + "%", x, y - height - 25);
		}
		batch.end();
		
		shr.begin();
		{
			shr.setColor(UI_COLOR_FRAME);
			shr.rect(x, y, width, -height);
		}
		shr.end();
	}
	
	public void dispose(){
		batch.dispose();
		shr.dispose();
		fontClassic.dispose();
	}
}
