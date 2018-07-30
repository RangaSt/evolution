package com.rhyn.evo.model;

public abstract class World {
	public static int size = 44;
	
	public static GameObject[][] cell;
	
	public static boolean wrapVertical = false;
	public static boolean wrapHorizontal = true;
	
	public static enum Season {WINTER(90, 2f, 4.5f), SPRING(92, 3.5f, 4.5f), SUMMER(92, 5f, 4.5f), AUTUMN(91, 3f, 4.5f);
		public int days;
		public float maxTemperature;
		public float maxMineral;
		Season(int days, float maxTemperature, float maxMineral){
			this.days = days;
			this.maxTemperature = maxTemperature;
			this.maxMineral = maxMineral;
		}
		int daysInYear(){
			return WINTER.days + SPRING.days + SUMMER.days + AUTUMN.days;
		}
		}
	public static Season season = Season.SPRING;
	
	public static float day = Season.WINTER.days;
	public static int year = 0;
	
	public static void updateCalendar(){
		if(day < season.daysInYear())day += .0416f;
		else{
			day -= season.daysInYear();
			year += 1;
		}
		if(day < Season.WINTER.days) season = Season.WINTER;
		else{
			if(day -  Season.WINTER.days < Season.SPRING.days) season = Season.SPRING;
			else{
				if(day -  (Season.WINTER.days + Season.SPRING.days) < Season.SUMMER.days) season = Season.SUMMER;
				else season = Season.AUTUMN;
			}
		}
	}
	
	public static int getSeasonDay(){
		int day = (int)World.day;
		
		if(day >= Season.WINTER.days)day-=Season.WINTER.days;
		if(day >= Season.SPRING.days)day-=Season.SPRING.days;
		if(day >= Season.SUMMER.days)day-=Season.SUMMER.days;
		
		return day; 
	}
	
	public static float getLineTemperature(int line){
		return (float)(line) / (float)size * (float)season.maxTemperature;
	}
	
	public static float getLineMineral(int line){
		if(line > (int)(size / 2))
			return 0f;
		return (float)(size - line) / (float)(size) * (float)season.maxMineral;
	}
	
	public static void reset(){
		year = 0;
		day = Season.WINTER.days;
	}
}
