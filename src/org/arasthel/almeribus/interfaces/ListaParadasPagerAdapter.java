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

public class ListaParadasPagerAdapter extends FragmentPagerAdapter{
	
	private ArrayList<Fragment> fragments;
	private ArrayList<String> titles;

	public ListaParadasPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
		titles = new ArrayList<String>();
	}
	
	public void addItem(Fragment f, String title) {
		fragments.add(f);
		titles.add(title);
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titles.get(position).toUpperCase();
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
