package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.*;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Brian.Bober on 12/21/2014.
 */


@Entity
@Table(name = "product_toolbar")
public class ProductToolbar implements Serializable
{

	private static final Logger log = Logger.getLogger(ProductToolbar.class);

	@Id
	@OneToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "align_horiz")
	@Enumerated(EnumType.STRING)
	private AlignHorizontal alignHorizontal;

	@Column(name = "align_vert")
	@Enumerated(EnumType.STRING)
	private AlignVertical alignVertical;

	@Column(name = "radio_type")
	@Enumerated(EnumType.STRING)
	private RadioType radioType;

	@Column(name = "radio_align")
	@Enumerated(EnumType.STRING)
	private RadioAlign radioAlign;

	@Column(name = "skin_type")
	@Enumerated(EnumType.STRING)
	private SkinType skinType; // null means default

	@Column(name = "load_preference")
	@Enumerated(EnumType.STRING)
	private ToolbarLoadPreferenceType loadPreference;

	// Skin color intentionally ommitted - don't allow custom for now

	protected ProductToolbar()
	{
	}

	public ProductToolbar(Product product)
	{
		this.product = product;
	}

	public AlignHorizontal getAlignHorizontal()
	{
		return alignHorizontal;
	}

	public void setAlignHorizontal(AlignHorizontal alignHorizontal)
	{
		this.alignHorizontal = alignHorizontal;
	}

	public AlignVertical getAlignVertical()
	{
		return alignVertical;
	}

	public void setAlignVertical(AlignVertical alignVertical)
	{
		this.alignVertical = alignVertical;
	}

	public RadioType getRadioType()
	{
		return radioType;
	}

	public void setRadioType(RadioType radioType)
	{
		this.radioType = radioType;
	}

	public Product getProduct()
	{
		return product;
	}

	public void setProduct(Product product)
	{
		this.product = product;
	}

	public RadioAlign getRadioAlign()
	{

		return radioAlign;
	}

	public void setRadioAlign(RadioAlign radioAlign)
	{
		this.radioAlign = radioAlign;
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
