package com.rhyn.evo;

import com.badlogic.gdx.graphics.Color;

public interface GameConstants {
	Color COLOR_BOT_GLOWED = new Color(.94f, .90f, .55f, 1f);
	Color COLOR_BOT_SELECTED = new Color(1f, 0f, 1f, 0.2f);
	Color COLOR_BOT_DEAD = new Color(.9f, .9f, .9f, 1f);
	
	int BOT_GENOME_SIZE = 64;
	int FIRST_GENERATION_BOT_COUNT = 1;
	int BOT_ENERGY_START = 25;
	float BOT_ENERGY_CAP = 256;
	int BOT_ENERGY_DEAD = 25;
	float BOT_ENERGY_MEAL_PENALTY = 1.1f;
	float BOT_MUTATION_CHANCE = 0.25f;
}
