/*
 * Aestheticodes recognises a different marker scheme that allows the
 * creation of aesthetically pleasing, even beautiful, codes.
 * Copyright (C) 2014  Aestheticodes
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.horizon.aestheticodes.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import uk.ac.horizon.aestheticodes.R;
import uk.ac.horizon.aestheticodes.properties.bindings.ColorImageBinding;
import uk.ac.horizon.aestheticodes.properties.bindings.VisibilityBinding;
import uk.ac.horizon.aestheticodes.controller.ExperienceManager;
import uk.ac.horizon.aestheticodes.model.Experience;
import uk.ac.horizon.aestheticodes.properties.Properties;

public class ExperienceActivity extends ActionBarActivity
{
	private ExperienceManager experienceManager;
	private Experience experience;
	private Properties properties;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		String experienceID = extras.getString("experience");

		experienceManager = ExperienceManager.get(this);
		experience = experienceManager.get(experienceID);

		setContentView(R.layout.experience);

		properties = new Properties(this, experience);
		properties.get("name").bindTo(R.id.experienceTitle);
		properties.get("description").bindTo(R.id.experienceDescription);
		properties.get("icon").bindTo(R.id.experienceIcon);
		properties.get("image").bindTo(new ColorImageBinding(R.id.experienceImage, R.id.experienceFloatingAction));
		//properties.get("editable").bindTo(new VisibilityBinding(R.id.experienceFloatingAction));
		properties.load();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.experience_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_delete:
				Log.i("", "Delete experience");
				AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
				confirmBuilder.setTitle(getResources().getString(R.string.experienceDeleteConfirmTitle, experience.getName()));
				confirmBuilder.setMessage(getResources().getString(R.string.experienceDeleteConfirmMessage, experience.getName()));
				confirmBuilder.setPositiveButton(R.string.deleteConfirm, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
						experience.setOp(Experience.Operation.remove);
						experienceManager.save();
						NavUtils.navigateUpTo(ExperienceActivity.this, new Intent(ExperienceActivity.this, ExperienceListActivity.class));
					}
				});
				confirmBuilder.setNegativeButton(R.string.deleteCancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialogInterface, int i)
					{
						// nothing
					}
				});

				confirmBuilder.create().show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void editExperience(View view)
	{
		Intent intent = new Intent(ExperienceActivity.this, ExperienceEditActivity.class);
		intent.putExtra("experience", experience.getId());

		startActivity(intent);
	}
}