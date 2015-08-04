package uk.ac.horizon.artcodes.source;

import android.content.Context;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import uk.ac.horizon.artcodes.account.Account;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class HTTPSource<T> extends UriSource<T>
{
	public static class Factory implements SourceFactory
	{

		@Override
		public String[] getPrefixes()
		{
			return new String[]{"http:", "https:"};
		}

		@Override
		public <T> Source<T> createSource(Account account, String uri, Type type)
		{
			return new HTTPSource<>(account, uri, type);
		}
	}

	private static RequestQueue requestQueue;

	private static final int timeout = 10000;

	public HTTPSource(Account account, String uri, Type type)
	{
		super(account, uri, type);
	}

	public static RequestQueue getQueue(Context context)
	{
		if (requestQueue == null)
		{
			requestQueue = Volley.newRequestQueue(context.getApplicationContext());
		}
		return requestQueue;
	}

	protected Map<String, String> getRequestHeaders()
	{
		return Collections.emptyMap();
	}

	@Override
	public void loadInto(final Target<T> target)
	{
		Request<?> request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response)
			{
				try {
					target.onLoaded(account.getGson().<T>fromJson(response, type));
				}
				catch (Exception e)
				{
					Log.e("HTTPSource", "Something went wrong while trying to parse response from the server. Most likely behind a wifi sign in page.", e);
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.e("", error.getMessage(), error);
			}
		})
		{
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError
			{
				return getRequestHeaders();
			}
		};

		RequestQueue requestQueue = getQueue(account.getContext());
		request.setRetryPolicy(new DefaultRetryPolicy(
				timeout,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		requestQueue.add(request);
	}
}
