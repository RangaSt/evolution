package com.rhyn.evo.view;

import com.badlogic.gdx.graphics.Color;

public interface UI_Constants {
	int UI_FIELD_MARGIN_LEFT = 32;
	int UI_FIELD_MARGIN_RIGHT = 32;
	int UI_FIELD_MARGIN_TOP = 92;
	int UI_FIELD_MARGIN_BOTTOM = 32;
	int UI_PADDING = 8;
	
	Color UI_COLOR_FRAME = new Color(0.50f, 0.50f, 0.50f, 1f);
	Color UI_COLOR_FRAME_SECONDARY = new Color(0.25f, 0.25f, 0.25f, 1f);
	Color UI_COLOR_GENOME = new Color(1f, 1f, 1f, 1f);
	Color UI_COLOR_GENOME_CURRENT = new Color(1f, 1f, 0f, 1f);
	Color UI_COLOR_GENOME_TEXT = new Color(0f, 0f, 0f, 1f);
	Color UI_COLOR_GENOME_MUTATED = new Color(0.5f, 0f, 0.5f, 1f);
	Color UI_COLOR_GENOME_MUTATED_TEXT = new Color(1f, 1f, 0f, 1f);
}
