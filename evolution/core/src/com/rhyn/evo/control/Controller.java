package com.rhyn.evo.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.rhyn.evo.GameConstants;
import com.rhyn.evo.model.Bot;
import com.rhyn.evo.model.GameObject;
import com.rhyn.evo.model.World;
import com.rhyn.evo.view.Renderer;

public class Controller implements GameConstants{
	
	private int counter = 0;
	
	private boolean pause = false;
	
	public static boolean log = false;
	public static int population = 0;
	
	public int skip = 0;
	
	public static int carnivoreCount;
	public static int herbivoreCount;
	public static int mineralCount;
	
	
	public Controller(){
		start();
	}
	
	public void inputs(){
		if(Gdx.input.isKeyJustPressed(Keys.S))
			pause = pause ? false : true;
		if(!Gdx.input.isKeyPressed(Keys.SPACE))
			counter = 7;
		if(Gdx.input.isKeyJustPressed(Keys.R))
			restart();
		if(Gdx.input.isKeyJustPressed(Keys.T))
			Renderer.showTemperatureMap = Renderer.showTemperatureMap ? false : true;
		if(Gdx.input.isKeyJustPressed(Keys.G))
			Renderer.showGrid = Renderer.showGrid ? false : true;
		if(Gdx.input.isKeyJustPressed(Keys.E))
			Renderer.showEnergy = Renderer.showEnergy ? false : true;
		if(Gdx.input.isKeyJustPressed(Keys.D))
			skip = 24;
		if(Gdx.input.isKeyJustPressed(Keys.M))
			skip = 720;
		if(Gdx.input.isKeyJustPressed(Keys.Y))
			skip = 8640;
	}
	
	public void update(float delta){	
		inputs();
		
		if(!pause){
			if(counter < 7)counter++;
			else{
				counter -= 7;
				World.updateCalendar();
				updateBots();
				
				while(skip > 0){
					skip--;
					World.updateCalendar();
					updateBots();
				}
			}
		}
		
		if(population == 0)
			restart();
	}
	
	public void updateBots(){
		//Primary cycle. Listening bots and they do things
		population = 0;
		carnivoreCount = 0;
		herbivoreCount = 0;
		mineralCount = 0;
		for(int i = 0; i < World.size; i++)
			for(int j = 0; j < World.size; j++)
				if(World.cell[i][j] != null){
					if(((Bot)World.cell[i][j]).getState() == Bot.State.LIVE)
						population+=1;
					int updateResult = ((Bot)World.cell[i][j]).update();
					int currentUpdate = 0;
					int updateMax = 10;
					
					
					while(updateResult == 0){
						updateResult = ((Bot)World.cell[i][j]).update();
						currentUpdate++;
						
						if(currentUpdate >= updateMax)
							break;
					}
					
					if(((Bot)(World.cell[i][j])).getState() == Bot.State.LIVE){
						((Bot)(World.cell[i][j])).decreaseEnergy();
						carnivoreCount += ((Bot)(World.cell[i][j])).getBehaviourCarnivore();
						herbivoreCount += ((Bot)(World.cell[i][j])).getBehaviourHerbivore();
						mineralCount += ((Bot)(World.cell[i][j])).getBehaviourMineral();
					}
					else{
						((Bot)(World.cell[i][j])).addRotting();
						if(((Bot)(World.cell[i][j])).getRotting() > 8640)
							removeBot(((Bot)(World.cell[i][j])));
					}
					if(updateResult == -1)
						killBot(World.cell[i][j]);
					
					
				}
		if(population > 0){
			carnivoreCount /= population;
			herbivoreCount /= population;
			mineralCount /= population;
		}
		//Secondary cycle. Here bot's commands doing
		for(int i = 0; i < World.size; i++)
			for(int j = 0; j < World.size; j++)
				if(World.cell[i][j] != null){
					if(World.cell[i][j].getX() != i || World.cell[i][j].getY() != j)
						moveBot(World.cell[i][j].getX(), World.cell[i][j].getY(), (Bot)World.cell[i][j]);
				}
	}
	
	public void start(){
		newGeneration();
		for(int i = 0; i < World.size; i++)
			System.out.println("Minerals at line " + i + ": " + World.getLineMineral(i));
	}
	
	public void newGeneration(){
		World.cell = new GameObject[World.size][World.size];
		for(int i = 0; i < FIRST_GENERATION_BOT_COUNT; i++){
			int x = (int)(Math.random()*(float)World.size);
			int y = (int)(Math.random()*(float)World.size);
			while(getCell(x, y) != null){
				x = (int)(Math.random()*(float)World.size);
				y = (int)(Math.random()*(float)World.size);
			}
			createBot(x, y);
		}
		
		for(int i = 0; i < World.size; i++)
			for(int j = 0; j < World.size; j++)
				if(World.cell[i][j] != null)
					((Bot)World.cell[i][j]).setGenomeDefault();
	}
	
	public void clear(){
		World.cell = null;
	}
	
	public void restart(){
		skip = 0;
		clear();
		World.reset();
		start();
	}
	
	public static GameObject getCell(int x, int y){
		return World.cell[x][y];			
	}
	
	public static void moveBot(int x, int y, Bot bot){
		for(int i = 0; i < World.size; i++)
			for(int j = 0; j < World.size; j++)
				if(World.cell[i][j] == bot)
					World.cell[i][j] = null;
		World.cell[x][y] = bot;
	}
	
	public static Bot createBot(int x, int y){
		if(getCell(x, y) == null)
			return (Bot)(World.cell[x][y] = new Bot(x, y));
		return null;
	}
	
	public static void removeBot(GameObject bot){
		for(int i = 0; i < World.size; i++)
			for(int j = 0; j < World.size; j++)
				if(World.cell[i][j] == bot)
					World.cell[i][j] = null;
		bot = null;
	}
	
	public static void killBot(GameObject bot){
		for(int i = 0; i < World.size; i++)
			for(int j = 0; j < World.size; j++)
				if(World.cell[i][j] == bot){
					((Bot)(World.cell[i][j])).setState(Bot.State.DEAD);
					((Bot)(World.cell[i][j])).setColor(COLOR_BOT_DEAD);
				}
	}
	
	public void dispose(){
		clear();
	}
}
