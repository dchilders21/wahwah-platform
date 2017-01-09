package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.ProductToolbar;
import com.wahwahnetworks.platform.data.entities.enums.*;

import javax.validation.constraints.NotNull;

@WebSafeModel
public class ProductToolbarModel
{
	@JsonProperty("align_horiz")
	@NotNull
	private AlignHorizontal alignHorizontal;

	@JsonProperty("align_vert")
	@NotNull
	private AlignVertical alignVertical;

	@JsonProperty("radio_type")
	@NotNull
	private RadioType radioType;

	@JsonProperty("radio_align")
	@NotNull
	private RadioAlign radioAlign;

	@JsonProperty("skin_type")
	private SkinType skinType; // null means default

	@JsonProperty("load_preference")
	private ToolbarLoadPreferenceType loadPreference;

	public ProductToolbarModel()
	{

	}

	public ProductToolbarModel(ProductToolbar toolbar)
	{
		this.setAlignHorizontal(toolbar.getAlignHorizontal());
		this.setAlignVertical(toolbar.getAlignVertical());
		this.setRadioType(toolbar.getRadioType());
		this.setRadioAlign(toolbar.getRadioAlign());
		this.setLoadPreference(toolbar.getLoadPreference());
		this.setSkinType(toolbar.getSkinType());
	}

	public AlignHorizontal getAlignHorizontal()
	{
		return alignHorizontal;
	}

	public void setAlignHorizontal(AlignHorizontal alignHorizontal)
	{
		this.alignHorizontal = alignHorizontal;
	}

	public RadioType getRadioType()
	{
		return radioType;
	}

	public void setRadioType(RadioType radioType)
	{
		this.radioType = radioType;
	}

	public RadioAlign getRadioAlign()
	{
		return radioAlign;
	}

	public void setRadioAlign(RadioAlign radioAlign)
	{
		this.radioAlign = radioAlign;
	}

	public AlignVertical getAlignVertical()
	{
		return alignVertical;
	}

	public void setAlignVertical(AlignVertical alignVertical)
	{
		this.alignVertical = alignVertical;
	}

	public SkinType getSkinType()
	{
		return skinType;
	}

	public void setSkinType(SkinType skinType)
	{
		this.skinType = skinType;
	}

	public ToolbarLoadPreferenceType getLoadPreference()
	{
		return loadPreference;
	}

	public void setLoadPreference(ToolbarLoadPreferenceType loadPreference)
	{
		this.loadPreference = loadPreference;
	}

}