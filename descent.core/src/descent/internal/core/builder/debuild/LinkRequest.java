package descent.internal.core.builder.debuild;

import descent.core.builder.IExecutableTarget;

public class LinkRequest extends BuildRequest
{
	/**
	 * The link target.
	 */
	public IExecutableTarget target;
	
	/**
	 * The full pathname where the resulting file should be output to.
	 */
	public String targetFilename;
	
	public LinkRequest()
	{
		setDefaults();
	}

	@Override
	public void setDefaults()
	{
		super.setDefaults();
		
		target = null;
		targetFilename = null;
	}



	@Override
	public RequestType getRequestType()
	{
		return RequestType.LINK;
	}
}
