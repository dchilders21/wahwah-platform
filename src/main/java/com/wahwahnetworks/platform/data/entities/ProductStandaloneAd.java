package com.wahwahnetworks.platform.data.entities;

import com.wahwahnetworks.platform.data.entities.enums.AlignHorizontal;
import com.wahwahnetworks.platform.data.entities.enums.AlignVertical;
import com.wahwahnetworks.platform.data.entities.enums.StandaloneAdFormat;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Brian.Bober on 1/15/2015.
 */


@Entity
@Table(name = "product_standalone_ad")
public class ProductStandaloneAd implements Serializable
{

	private static final Logger log = Logger.getLogger(ProductStandaloneAd.class);

	@Id
	@OneToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "ad_format")
	@Enumerated(EnumType.STRING)
	private StandaloneAdFormat adFormat;


	/* Page alignment */
	@Column(name = "align_horiz")
	@Enumerated(EnumType.STRING)
	private AlignHorizontal alignHorizontal;

	@Column(name = "align_vert")
	@Enumerated(EnumType.STRING)
	private AlignVertical alignVertical;

	/* Expansion alignment */

	@Column(name = "expansion_align_horiz")
	@Enumerated(EnumType.STRING)
	private AlignHorizontal expansionAlignHorizontal;

	@Column(name = "expansion_align_vert")
	@Enumerated(EnumType.STRING)
	private AlignVertical expansionAlignVertical;

	@Column(name = "inad_breakout")
	private boolean inAdBreakout = false;

	@Column(name = "outstream_autoload")
	private boolean outstreamAutoload = false;

	@Column(name = "outstream_triggerId")
	private String outstreamTriggerId;

	@Column(name = "outstream_float")
	private boolean outstreamFloat = false;

	protected ProductStandaloneAd()
	{
	}

	public ProductStandaloneAd(Product product)
	{
		this.product = product;
	}

	/* Skin type and color intentionally ommitted */

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

	public Product getProduct()
	{
		return product;
	}

	public void setProduct(Product product)
	{
		this.product = product;
	}

	public StandaloneAdFormat getAdFormat()
	{
		return adFormat;
	}

	public void setAdFormat(StandaloneAdFormat adFormat)
	{
		this.adFormat = adFormat;
	}

	public AlignHorizontal getExpansionAlignHorizontal()
	{
		return expansionAlignHorizontal;
	}

	public void setExpansionAlignHorizontal(AlignHorizontal expansionAlignHorizontal)
	{
		this.expansionAlignHorizontal = expansionAlignHorizontal;
	}

	public AlignVertical getExpansionAlignVertical()
	{
		return expansionAlignVertical;
	}

	public void setExpansionAlignVertical(AlignVertical expansionAlignVertical)
	{
		this.expansionAlignVertical = expansionAlignVertical;
	}

	public boolean getBreakoutAd() {
		return inAdBreakout;
	}

	public void setBreakoutAd(boolean breakoutAd) {
		this.inAdBreakout = breakoutAd;
	}

	public boolean isOutstreamAutoload() {
		return outstreamAutoload;
	}

	public void setOutstreamAutoload(boolean outstreamAutoload) {
		this.outstreamAutoload = outstreamAutoload;
	}

	public String getOutstreamTriggerId() {
		return outstreamTriggerId;
	}

	public void setOutstreamTriggerId(String outstreamTriggerId) {
		this.outstreamTriggerId = outstreamTriggerId;
	}

	public boolean isOutstreamFloat() {
		return outstreamFloat;
	}

	public void setOutstreamFloat(boolean outstreamFloat) {
		this.outstreamFloat = outstreamFloat;
	}
}
