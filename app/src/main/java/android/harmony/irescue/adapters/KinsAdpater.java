package android.harmony.irescue.adapters;

import android.harmony.irescue.R;
import android.harmony.irescue.model.KinModel;
import android.harmony.irescue.utility.ColorGenerator;
import android.harmony.irescue.utility.TextDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class KinsAdpater extends RecyclerView.Adapter<KinsAdpater.KinsViewHolder> {
    List<KinModel> mKinModelList;

    public KinsAdpater(List<KinModel> kinModelList) {
        mKinModelList = kinModelList;
    }

    @NonNull
    @Override
    public KinsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_kin_layout, null);

        return new KinsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KinsViewHolder holder, int position) {
        KinModel kinModel = mKinModelList.get(position);
        holder.bind(kinModel);
    }

    @Override
    public int getItemCount() {
        return mKinModelList.size();
    }

    public void addKin(List<KinModel> kinModelList){
        this.mKinModelList=kinModelList;
        notifyDataSetChanged();
    }
    public class KinsViewHolder extends RecyclerView.ViewHolder {
        TextView kinName, kinPhoneNumber, relationship;
        ImageView kinImageView;
        ColorGenerator mGenerator=ColorGenerator.MATERIAL;

        public KinsViewHolder(View itemView) {
            super(itemView);
            kinName = itemView.findViewById(R.id.kin_name);
            kinPhoneNumber = itemView.findViewById(R.id.kin_phone_number);
            relationship = itemView.findViewById(R.id.relationship);
            kinImageView = itemView.findViewById(R.id.image_view);
        }

        public void bind(KinModel kinModel) {
            String letter="";
            kinName.setText(kinModel.getName());
            kinPhoneNumber.setText(kinModel.getPhoneNummber());
            relationship.setText(kinModel.getRelationship());
            if(kinModel.getName().trim().contains(" ")){
                String[] letters=kinModel.getName().split(" ");
                String firstLetter=String.valueOf(letters[0].charAt(0));
                String lastLetter=String.valueOf(letters[1].charAt(0));
                letter=firstLetter+lastLetter;
            }else{
                letter=String.valueOf(kinModel.getName().charAt(0))+String.valueOf(kinModel.getName().charAt(1));
            }
            System.out.println("miles"+letter);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(letter, mGenerator.getRandomColor());
            kinImageView.setImageDrawable(drawable);
        }
    }
}
