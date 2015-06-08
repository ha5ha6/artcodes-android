/*
 * Aestheticodes recognises a different marker scheme that allows the
 * creation of aesthetically pleasing, even beautiful, codes.
 * Copyright (C) 2013-2015  The University of Nottingham
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
package uk.ac.horizon.aestheticodes.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import uk.ac.horizon.aestheticodes.R;
import uk.ac.horizon.aestheticodes.activities.ExperienceEditActivity;
import uk.ac.horizon.aestheticodes.controllers.ChildController;
import uk.ac.horizon.aestheticodes.controllers.Controller;
import uk.ac.horizon.aestheticodes.controllers.adapters.IntAdapter;
import uk.ac.horizon.aestheticodes.controllers.adapters.MarkerRegionAdapter;
import uk.ac.horizon.aestheticodes.model.Experience;

public class EditMarkerRegionSizeDialog extends DialogFragment
{
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		final ExperienceEditActivity activity = ((ExperienceEditActivity) getActivity());
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		final LayoutInflater inflater = getActivity().getLayoutInflater();
		@SuppressLint("InflateParams")
		final View view = inflater.inflate(R.layout.settings_range, null);

		final Controller<Experience> controller = new ChildController<>(activity, view);
		controller.bindView(R.id.sliderMin, new IntAdapter<Experience>("minRegions", 3, 20)
		{
			@Override
			public void setValue(Controller<Experience> controller, Object value)
			{
				super.setValue(controller, value);
				Experience experience = controller.getModel();
				if(experience.getMinRegions() > experience.getMaxRegions())
				{
					experience.setMaxRegions(experience.getMinRegions());
					controller.notifyChanges("maxRegions");
				}
			}
		});
		controller.bindView(R.id.sliderMax, new IntAdapter<Experience>("maxRegions", 3, 20)
		{
			@Override
			public void setValue(Controller<Experience> controller, Object value)
			{
				super.setValue(controller, value);
				Experience experience = controller.getModel();
				if(experience.getMaxRegions() < experience.getMinRegions())
				{
					experience.setMinRegions(experience.getMaxRegions());
					controller.notifyChanges("minRegions");
				}
			}
		});
		controller.bindView(R.id.sliderValue, new MarkerRegionAdapter(activity));

		builder.setTitle(getString(R.string.property_set, getString(R.string.regions)));
		String description = getString(R.string.regions_desc);
		if (description != null)
		{
			builder.setMessage(description);
		}
		builder.setView(view);
		builder.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				controller.unbind();
			}
		});

		return builder.create();
	}
}