package com.mediamonks.googleflip.util;

/**
 * Interface for navigation
 */
public interface Navigator {
	/**
	 * Navigate to another fragment/activity
	 *
	 * @param name
	 * @return true if the name is valid else false.
	 */
	boolean navigateTo(String name);
}
