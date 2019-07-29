package sg.edu.rp.c346.p12webservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IncidentAdapter extends ArrayAdapter<Incident> {

    private ArrayList<Incident> alIncident;
    private Context context;

    public IncidentAdapter(Context context, int resource, ArrayList<Incident> objects){
        super(context, resource, objects);
        alIncident = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);

        TextView tvType = rowView.findViewById(R.id.tvType);
        TextView tvDesc = rowView.findViewById(R.id.tvDesc);

        Incident incident = alIncident.get(position);

        tvType.setText(incident.getType());
        tvDesc.setText(incident.getMessage());

        return rowView;
    }

}

