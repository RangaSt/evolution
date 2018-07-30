package com.rhyn.evo.model;

public abstract class GameObject {
	protected int x, y;
	protected boolean glowed;
	protected boolean selected;
	
	public GameObject(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public boolean isGlowed(){
		return glowed;
	}
	
	public void setGlowed(boolean glowed){
		this.glowed = glowed;
	}
	
	public boolean isSelected(){
		return selected;
	}
}
