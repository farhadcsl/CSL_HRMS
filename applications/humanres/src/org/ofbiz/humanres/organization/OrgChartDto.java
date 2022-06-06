/**
 * 
 */
package org.ofbiz.humanres.organization;

/**
 * @author tariqul
 * 
 */
public class OrgChartDto {
	private String title;
	private String groupTitleColor;
	private String itemTitleColor;
	private int id;
	private int parent;
	private String partyId;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrgChartDto [title=" + title + ", groupTitleColor="
				+ groupTitleColor + ", itemTitleColor=" + itemTitleColor
				+ ", id=" + id + ", parent=" + parent + ", partyId=" + partyId
				+ ", getTitle()=" + getTitle() + ", getId()=" + getId()
				+ ", getParent()=" + getParent() + ", getItemTitleColor()="
				+ getItemTitleColor() + ", getGroupTitleColor()="
				+ getGroupTitleColor() + ", getPartyId()=" + getPartyId()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
	
	public OrgChartDto() {
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * @param title
	 * @param groupTitleColor
	 * @param itemTitleColor
	 * @param id
	 * @param parent
	 * @param partyId
	 */
	public OrgChartDto(String title, String groupTitleColor,
			String itemTitleColor, int id, int parent, String partyId) {
		super();
		this.title = title;
		this.groupTitleColor = groupTitleColor;
		this.itemTitleColor = itemTitleColor;
		this.id = id;
		this.parent = parent;
		this.partyId = partyId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the parent
	 */
	public int getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}

	/**
	 * @return the itemTitleColor
	 */
	public String getItemTitleColor() {
		return itemTitleColor;
	}

	/**
	 * @param itemTitleColor the itemTitleColor to set
	 */
	public void setItemTitleColor(String itemTitleColor) {
		this.itemTitleColor = itemTitleColor;
	}

	/**
	 * @return the groupTitleColor
	 */
	public String getGroupTitleColor() {
		return groupTitleColor;
	}

	/**
	 * @param groupTitleColor the groupTitleColor to set
	 */
	public void setGroupTitleColor(String groupTitleColor) {
		this.groupTitleColor = groupTitleColor;
	}

	/**
	 * @return the partyId
	 */
	public String getPartyId() {
		return partyId;
	}

	/**
	 * @param partyId the partyId to set
	 */
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

}
