/*******************************************************************************
 * Copyright (c) 2013 Jorge Mart’n Espinosa (Arasthel).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jorge Mart’n Espinosa (Arasthel) - initial API and implementation
 ******************************************************************************/
package org.arasthel.almeribus.interfaces;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class InfoFragmentPagerAdapter extends FragmentPagerAdapter{
	
	private ArrayList<Fragment> fragments;

	public InfoFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
	}
	
	public void addItem(Fragment f) {
		fragments.add(f);
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return "WASSAP";
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
