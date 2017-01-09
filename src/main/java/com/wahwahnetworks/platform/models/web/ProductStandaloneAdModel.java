package com.wahwahnetworks.platform.models.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wahwahnetworks.platform.annotations.WebSafeModel;
import com.wahwahnetworks.platform.data.entities.ProductStandaloneAd;
import com.wahwahnetworks.platform.data.entities.enums.AlignHorizontal;
import com.wahwahnetworks.platform.data.entities.enums.AlignVertical;
import com.wahwahnetworks.platform.data.entities.enums.StandaloneAdFormat;

import javax.validation.constraints.NotNull;

/**
 * Created by Brian.Bober on 1/15/2015.
 */

@WebSafeModel
public class ProductStandaloneAdModel
{
	@JsonProperty("align_horiz")
	@NotNull
	private AlignHorizontal alignHorizontal;

	@JsonProperty("align_vert")
	@NotNull
	private AlignVertical alignVertical;

	@JsonProperty("expansion_align_horiz")
	private AlignHorizontal expansionAlignHorizontal;

	@JsonProperty("expansion_align_vert")
	private AlignVertical expansionAlignVertical;

	@JsonProperty("ad_format")
	@NotNull
	private StandaloneAdFormat adFormat;

	@JsonProperty("inad_breakout")
	private Boolean inAdBreakout;

	@JsonProperty("outstream_autoload")
	private Boolean outstreamAutoload;

	@JsonProperty("outstream_triggerId")
	private String outstreamTriggerId;

	@JsonProperty("outstream_float")
	private Boolean outstreamFloat;

	public ProductStandaloneAdModel()
	{

	}

	public ProductStandaloneAdModel(ProductStandaloneAd ad)
	{
		this.setAlignHorizontal(ad.getAlignHorizontal());
		this.setAlignVertical(ad.getAlignVertical());
		this.setExpansionAlignHorizontal(ad.getExpansionAlignHorizontal());
		this.setExpansionAlignVertical(ad.getExpansionAlignVertical());
		this.setAdFormat(ad.getAdFormat());
		this.setBreakoutAd(ad.getBreakoutAd());
		this.setOutstreamAutoload(ad.isOutstreamAutoload());
		this.setOutstreamTriggerId(ad.getOutstreamTriggerId());
		this.setOutstreamFloat(ad.isOutstreamFloat());
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

	public Boolean getBreakoutAd() {
		return inAdBreakout;
	}

	public void setBreakoutAd(Boolean breakoutAd) {
		this.inAdBreakout = breakoutAd;
	}

	public Boolean getOutstreamAutoload() {
		return outstreamAutoload;
	}

	public void setOutstreamAutoload(Boolean outstreamAutoload) {
		this.outstreamAutoload = outstreamAutoload;
	}

	public String getOutstreamTriggerId() {
		return outstreamTriggerId;
	}

	public void setOutstreamTriggerId(String outstreamTriggerId) {
		this.outstreamTriggerId = outstreamTriggerId;
	}

	public Boolean getOutstreamFloat() {
		return outstreamFloat;
	}

	public void setOutstreamFloat(Boolean outstreamFloat) {
		this.outstreamFloat = outstreamFloat;
	}

}