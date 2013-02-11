package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class QueryFieldButton extends Button implements OnClickListener {
	
	private Bugzilla mBugz;
	private Context mContext;
	//! the sibling edit text item
	private EditText mEdit;
	private String mFieldName;
	private String mPromptTitle;
	
	
	public QueryFieldButton (Context context) {
	    super(context);
	}

	public QueryFieldButton (Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    
	    init(context, attrs);
	}

	public QueryFieldButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		
		mContext = context;

		Context appContext = context.getApplicationContext();
		BugzillaApplication app = (BugzillaApplication) appContext;
		mBugz = app.getBugzilla();
		this.setOnClickListener(this);		
	}
	
	public void configure(String databaseField, String promptTitle, EditText editItem) {
		mEdit = editItem;
		mFieldName = databaseField;
		mPromptTitle = promptTitle;
	}
	
	
	@Override
	public void onClick(View v) {

		if (mEdit == null)
			return;
				
		/* get the current text */
		Editable items = mEdit.getText();

		/* get current items */
		StringTokenizer tokens = new StringTokenizer(items.toString(),
				",");
		Set<String> currentValues = new HashSet<String>();
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			token = token.trim();
			if (!token.isEmpty())
				currentValues.add(token);
		}
		
		/* get available items */
		ArrayList<String> statusValues = mBugz
				.getValues(mFieldName);
		
		
		final int size = statusValues.size();
		final boolean[] selected = new boolean[size];
		int index = 0;
		
		List<CharSequence> availableValues = new ArrayList<CharSequence>();
		for (String value : statusValues) {
			availableValues.add(value);
			
			selected[index] = currentValues.contains(value);
			index++;
		}
		final CharSequence[] available = availableValues.toArray(new CharSequence[size]);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setTitle(mPromptTitle)
				.setMultiChoiceItems(
						available, selected,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								
								 selected[which] = isChecked;
							}
						})

				
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int id) {
								
								String newText = new String();
								for (int i = 0; i < size; ++i) {
									if (selected[i]) {
										newText += available[i];
										newText += ", ";
									}
								}
								mEdit.setText(newText);
							}
						})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
							}
						});

		AlertDialog alert = builder.create();
		alert.show();

	}
}
