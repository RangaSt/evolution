package com.rhyn.evo.model;

import com.badlogic.gdx.graphics.Color;
import com.rhyn.evo.GameConstants;
import com.rhyn.evo.control.Controller;

public class Bot extends GameObject implements GameConstants{

	/*
	 * Bot genome commands:
	 * 0..7 - перемещение
	 * 8..15 - поворот
	 * 16..23 - посмотреть
	 * 24 - фотосинтез
	 * 25 - съесть
	 * 26 - сколько у мен€ энергии?
	 * 27 - сколько у мен€ соседей?
	 * 28 - создать потомка
	 * 29 - извлечь минералы
	 * 30 - передать энергию родственнику
	 * 31..63 - условный переход
	 */
	
	private int[] genome = new int[BOT_GENOME_SIZE];
	private int genomeCurrent = 0;
	
	private int genomeMutated = 0;
	private boolean mutant = false;
	
	private float energy = BOT_ENERGY_START;
	
	private int rotting = 0;
	
	private int behaviourHerbivore = 50;
	private int behaviourCarnivore = 50;
	private int behaviourMineral = 50;
	
	public static enum Direction {RIGHT(1, 0), UPRIGHT(1, 1), UP(0, 1), UPLEFT(-1, 1),
					LEFT(-1, 0), DOWNLEFT(-1, -1), DOWN(0, -1), DOWNRIGHT(1, -1);
		public int dx;
		public int dy;
		Direction(int dx, int dy){
			this.dx = dx;
			this.dy = dy;
		}
	}
	
	public static enum State {LIVE, DEAD};
	
	public static enum Behaviour {CARNIVORE, HERBIVORE, MINERAL};
	
	private State state;
	
	private Color color;
	
	private Direction direction = Direction.RIGHT;

	public Bot(int x, int y){
		super(x, y);
		color = new Color(0f, 1f, 0f, 1f);
		state = State.LIVE;
		for(int i = 0; i < BOT_GENOME_SIZE; i++)
			genome[i] = 0;
	}
	
	public int update()
	/*
	 * Returns:
	 * 0 - need to update again
	 * 1 - done
	 *-1 - died
	 */
	{
		if(state == State.LIVE){
			if(energy <= 0)
				return -1;
			if(energy >= BOT_ENERGY_CAP){
				if(orderBirth()) 
					return 1;
				return -1;
			}
			if(genome[genomeCurrent] < 8){
				//Move
				int direction = this.direction.ordinal() + genome[genomeCurrent];
				if(direction >= 8)
					direction -= 8;
				int moveResult = orderMove(Direction.values()[direction]);
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + moveResult + 2 >= BOT_GENOME_SIZE ?
										genomeCurrent + moveResult + 2 - BOT_GENOME_SIZE : 
										genomeCurrent + moveResult + 2]);
				
				return 1;
			}
			
			if(genome[genomeCurrent] < 16){
				//Rotate
				int direction = this.direction.ordinal() + genome[genomeCurrent] - 8;
				if(direction >= 8)
					direction -= 8;
				orderRotate(Direction.values()[direction]);
				
				setGenomeCurrent(genomeCurrent + 1);
				
				return 0;
			}
			
			if(genome[genomeCurrent] < 24){
				//Look around
				int direction = this.direction.ordinal() + genome[genomeCurrent] - 16;
				if(direction >= 8)
					direction -= 8;
				
				int lookResult = orderLook(Direction.values()[direction]);
				
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + lookResult + 2 >= BOT_GENOME_SIZE ?
										genomeCurrent + lookResult + 2 - BOT_GENOME_SIZE : 
										genomeCurrent + lookResult + 2]);
				
				return 0;
			}
			
			if(genome[genomeCurrent] == 24){
				//Produce sunlight energy
				orderEatSunlight();
				
				setGenomeCurrent(genomeCurrent + 1);
				return 1;
			}
			
			if(genome[genomeCurrent] == 25){
				//Eat
				int direction = this.direction.ordinal() + genome[genomeCurrent];
				while(direction >= 8)
					direction -= 8;
				int eatResult = orderEatBot(Direction.values()[direction]);
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + eatResult + 2 >= BOT_GENOME_SIZE ?
										genomeCurrent + eatResult + 2 - BOT_GENOME_SIZE : 
										genomeCurrent + eatResult + 2]);
				
				return 1;
			}
			
			if(genome[genomeCurrent] == 26){
				//How much energy?
				int parametr = genomeCurrent + genome[genomeCurrent + 1 >= BOT_GENOME_SIZE ?
						genomeCurrent + 1 - BOT_GENOME_SIZE : 
						genomeCurrent + 1];
				int answer = 2;
				if(energy < parametr * 4)
					answer = 3;
					
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + answer >= BOT_GENOME_SIZE ?
										genomeCurrent + answer - BOT_GENOME_SIZE : 
										genomeCurrent + answer]);
				
				return 0;
			}
			
			if(genome[genomeCurrent] == 27){
				//How much neighbors?
				int answer = 0;
				for(int i = -1; i < 2; i++)
					for(int j = -1; j < 2; j++){
						int goalX = x + i, goalY = y + j;
						if(goalX < 0 || goalX >= World.size){
							if(World.wrapHorizontal)
								goalX = goalX < 0 ? goalX + World.size : goalX - World.size;
							else
								continue;
						}
						if(goalY < 0 || goalY >= World.size){
							if(World.wrapVertical)
								goalY = goalY < 0 ? goalY + World.size : goalY - World.size;
							else
								continue;
						}
						if(World.cell[goalX][goalY] != null)
							if(World.cell[goalX][goalY] != this)
								answer++;
					}
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + answer >= BOT_GENOME_SIZE ?
										genomeCurrent + answer - BOT_GENOME_SIZE : 
										genomeCurrent + answer]);
				
				return 0;
			}
			
			if(genome[genomeCurrent] == 28){
				int translate = 2;
				if(energy >= BOT_ENERGY_CAP / 4 * 3){
					if(orderBirth())
						translate = 1;
				}
				else
					translate = 3;
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + translate >= BOT_GENOME_SIZE ?
						genomeCurrent + translate - BOT_GENOME_SIZE : 
						genomeCurrent + translate]);
				return 1;
				
			}    
			
			if(genome[genomeCurrent] == 29){
				//Extract minerals
				orderEatMinerals();
				
				setGenomeCurrent(genomeCurrent + 1);
				return 1;
			}
			
			if(genome[genomeCurrent] == 30){
				//Share energy
				int result = orderShare();
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent + result >= BOT_GENOME_SIZE ?
						genomeCurrent + result - BOT_GENOME_SIZE : 
						genomeCurrent + result]);
				return 0;
				
			}  
			
			if(genome[genomeCurrent] < 64){
				setGenomeCurrent(genomeCurrent + genome[genomeCurrent]);

				return 0;
			}
			
			return 1;
		}
		
		return 1;
	}
	
	public int orderMove(Direction direction)
	/*
	 * Returned int:
	 * -1 Ц this cell doesn't exist
	 *  0 Ц there was empty cell
	 *  1 - there was another bot
	 *  2 - there was dead bot
	 */
	{
		int goalX = x + direction.dx, goalY = y + direction.dy;
		if(goalX < 0 || goalX >= World.size){
			if(World.wrapHorizontal)
				goalX = goalX < 0 ? goalX + World.size : goalX - World.size;
			else
				return -1; //Cell is not exist
		}
		if(goalY < 0 || goalY >= World.size){
			if(World.wrapVertical)
				goalY = goalY < 0 ? goalY + World.size : goalY - World.size;
			else
				return -1; //Cell is not exist
		}
		Bot goalCell = (Bot)Controller.getCell(goalX, goalY);
		if(goalCell == null){
			x = goalX;
			y = goalY;
			return 0;
		}
		
		if(goalCell.getState() == State.LIVE){
			return 1;
		}
		else{ 
			if(goalCell.getState() == State.DEAD)
				return 2;
		}
		return -1;
	}
	
	public void orderRotate(Direction direction){
		this.direction = direction;
	}
	
	public int orderLook(Direction direction)
	/*
	 * Returns:
	 *-1 Ц cell doesn't exist
	 * 0 Ц cell is empty
	 * 1 Ц in this cell another bot
	 * 2 - in this cell dead bot
	 * 3 - in this cell family bot
	 */
	{
		int goalX = x + direction.dx, goalY = y + direction.dy;
		if(goalX < 0 || goalX >= World.size){
			if(World.wrapHorizontal)
				goalX = goalX < 0 ? goalX + World.size : goalX - World.size;
			else
				return -1; //Cell is not exist
		}
		if(goalY < 0 || goalY >= World.size){
			if(World.wrapVertical)
				goalY = goalY < 0 ? goalY + World.size : goalY - World.size;
			else
				return -1; //Cell is not exist
		}
		Bot goalCell = (Bot)Controller.getCell(goalX, goalY);
		if(goalCell == null){
			return 0;
		}
		
		if(goalCell.getState() == State.LIVE){
			if(isFamily(goalCell))
				return 3;
			return 1;
		}
		else{ 
			if(goalCell.getState() == State.DEAD)
				return 2;
		}
		return -1;
	}
	
	public void orderEatSunlight(){
		energy += World.getLineTemperature(y);
		addBehaviour(Behaviour.HERBIVORE);
	}
	
	public void orderEatMinerals(){
		energy += World.getLineMineral(y);
		addBehaviour(Behaviour.MINERAL);
	}
	
	public int orderEatBot(Direction direction)
	/*
	 * Returns:
	 *-1 Ц cell doesn't exist
	 * 0 Ц cell is empty
	 * 1 Ц in this cell another bot
	 * 2 - in this cell dead bot
	 * 3 - in this cell family bot
	 */
	{
		int goalX = x + direction.dx, goalY = y + direction.dy;
		if(goalX < 0 || goalX >= World.size){
			if(World.wrapHorizontal)
				goalX = goalX < 0 ? goalX + World.size : goalX - World.size;
			else
				return -1; //Cell is not exist
		}
		if(goalY < 0 || goalY >= World.size){
			if(World.wrapVertical)
				goalY = goalY < 0 ? goalY + World.size : goalY - World.size;
			else
				return -1; //Cell is not exist
		}
		Bot goalCell = (Bot)Controller.getCell(goalX, goalY);
		if(goalCell == null){
			return 0;
		}
		
		if(goalCell.getState() == State.LIVE){
			if(isFamily(goalCell)){
				energy += goalCell.energy / BOT_ENERGY_MEAL_PENALTY;
				Controller.removeBot(goalCell);
				addBehaviour(Behaviour.CARNIVORE);
				return 3;
			}
			energy += goalCell.energy / BOT_ENERGY_MEAL_PENALTY;
			Controller.removeBot(goalCell);
			addBehaviour(Behaviour.CARNIVORE);
			return 1;
		}
		else{ 
			if(goalCell.getState() == State.DEAD){
				energy += BOT_ENERGY_DEAD;
				Controller.removeBot(goalCell);
				addBehaviour(Behaviour.CARNIVORE);
				return 2;
			}
		}
		return -1;
	}
	
	public boolean orderBirth(){
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++){
				int goalX = x + i, goalY = y + j;
				if(goalX < 0 || goalX >= World.size){
					if(World.wrapHorizontal)
						goalX = goalX < 0 ? goalX + World.size : goalX - World.size;
					else
						continue;
				}
				if(goalY < 0 || goalY >= World.size){
					if(World.wrapVertical)
						goalY = goalY < 0 ? goalY + World.size : goalY - World.size;
					else
						continue;
				}
				if(World.cell[goalX][goalY] == null){
					Bot bot = Controller.createBot(goalX, goalY);
					if(bot!=null)
						bot.child(this);
					else
						return false;
					energy /= 2;
					return true;
				}
			}
		return false;
	}
	
	public int orderShare()
	/*
	 * Returns:
	 * 1 Ц can't share anybody
	 * 2 - share successful
	 */
	{
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++){
				int goalX = x + i, goalY = y + j;
				if(goalX < 0 || goalX >= World.size){
					if(World.wrapHorizontal)
						goalX = goalX < 0 ? goalX + World.size : goalX - World.size;
					else
						continue;
				}
				if(goalY < 0 || goalY >= World.size){
					if(World.wrapVertical)
						goalY = goalY < 0 ? goalY + World.size : goalY - World.size;
					else
						continue;
				}
				Bot bot = (Bot)World.cell[goalX][goalY];
				if(bot != null){
					if(isFamily(bot)){
						bot.setEnergy(energy / 2);
						energy /= 2;
						return 2;
					}
						
				}
			}
		return 1;
	}
	
	public void addBehaviour(Behaviour behaviour){
		behaviourCarnivore--;
		behaviourHerbivore--;
		behaviourMineral--;
		switch(behaviour){
		case CARNIVORE:
			behaviourCarnivore+=3;
			break;
		case HERBIVORE:
			behaviourHerbivore+=3;
			break;
		case MINERAL:
			behaviourMineral+=3;
			break;
		default:
			break;
		}
		if(behaviourCarnivore < 0)behaviourCarnivore = 0;
		if(behaviourHerbivore < 0)behaviourHerbivore = 0;
		if(behaviourMineral < 0)behaviourMineral = 0;
		if(behaviourCarnivore > 100)behaviourCarnivore = 100;
		if(behaviourHerbivore > 100)behaviourHerbivore = 100;
		if(behaviourMineral > 100)behaviourMineral = 100;
		setColor(new Color((float)(behaviourCarnivore)/100f, (float)(behaviourHerbivore)/100f, (float)(behaviourMineral)/100f, 1f));
	}
	
	public boolean isFamily(Bot bot){
		int err = 0;
		for(int i = 0; i < BOT_GENOME_SIZE; i++){
			if(genome[i] != bot.genome[i])
				err++;
			if(err > 1)
				return false;
		}
		
		return true;
	}
	
	public void child(Bot parent){
		behaviourCarnivore = parent.getBehaviourCarnivore();
		behaviourHerbivore = parent.getBehaviourHerbivore();
		behaviourMineral = parent.getBehaviourMineral();
		for(int i = 0; i < BOT_GENOME_SIZE; i++)
			genome[i] = parent.getGenome()[i];
		if(Math.random() <= BOT_MUTATION_CHANCE)
			mutate();
	}
	
	public void mutate(){
		int genomeMutated = (int)(Math.random()*(float)BOT_GENOME_SIZE);
		mutant = true;
		this.genomeMutated = genomeMutated;
		genome[genomeMutated] = (int)(Math.random()*(float)BOT_GENOME_SIZE);
	}
	
	public void setGenomeCurrent(int genomeCurrent){
		this.genomeCurrent = genomeCurrent >= BOT_GENOME_SIZE ? genomeCurrent - BOT_GENOME_SIZE : genomeCurrent;
	}
	
	public void setGenomeRandom(){
		//for(int i = 0; i < genome.length; i++)
			//genome[i] = (int)(Math.random() * (double)BOT_GENOME_SIZE);
		
		for(int i = 0; i < genome.length; i++)
			genome[i] = (int)(Math.random() * 30);
	}
	
	public void setGenomeDefault(){
		for(int i = 0; i < genome.length; i++)
			genome[i] = 24;
	}
	
	public int[] getGenome(){
		return genome;
	}
	
	public void setGenome(int[] genome){
		for(int i = 0; i < genome.length; i++)
			this.genome[i] = genome[i];
	}
	
	public int getGenomeCurrent(){
		return genomeCurrent;
	}
	
	public Direction getDirection(){
		return direction;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public Color getColor(){
		return color;
	}
	
	public void setState(State state){
		this.state = state;
	}
	
	public State getState(){
		return state;
	}
	
	public int getGenomeMutated(){
		return genomeMutated;
	}
	
	public boolean isMutant(){
		return mutant;
	}
	
	public float getEnergy(){
		return energy;
	}
	
	public void setEnergy(float energy){
		this.energy = energy;
	}
	
	public void decreaseEnergy(){
		energy--;
	}
	
	public void addRotting(){
		//rotting++;
	}
	public int getRotting(){
		return rotting;
	}

	public int getBehaviourHerbivore() {
		return behaviourHerbivore;
	}

	public int getBehaviourCarnivore() {
		return behaviourCarnivore;
	}

	public int getBehaviourMineral() {
		return behaviourMineral;
	}
}
