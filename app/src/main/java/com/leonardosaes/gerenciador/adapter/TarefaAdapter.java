package com.leonardosaes.gerenciador.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leonardosaes.gerenciador.R;
import com.leonardosaes.gerenciador.models.Task;
import com.leonardosaes.gerenciador.activities.ListaTarefasActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {

    private List<Task> listaDeTarefas; // Lista de tarefas a serem exibidas
    private DateTimeFormatter dateFormatter; // Para formatar a data
    private Context context;

    public TarefaAdapter(List<Task> listaDeTarefas, Context context) {
        this.listaDeTarefas = listaDeTarefas;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Define o formato da data
        this.context = context;
    }

    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item da tarefa (item_tarefa.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarefa, parent, false);
        return new TarefaViewHolder(view); // Retorna um novo ViewHolder com a View inflada
    }


    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        // Obtém a tarefa da posição atual na lista
        // Verifica se a lista não é nula e se a posição é válida
        if (listaDeTarefas == null || position >= listaDeTarefas.size() || listaDeTarefas.get(position) == null) {
            Log.e("TarefaAdapter", "Erro: listaDeTarefas nula, posição inválida ou tarefa nula na posição " + position);
            return;
        }

        Task tarefa = listaDeTarefas.get(position);

        // Define os valores dos TextViews do ViewHolder com os dados da tarefa
        holder.textTituloTarefa.setText(tarefa.getTitulo());
        holder.textDescricaoTarefa.setText(tarefa.getDescricao());
        holder.textStatusTarefa.setText(tarefa.getStatusTarefa());

        LocalDate prazoFinal = tarefa.getPrazoFinal();
        if (prazoFinal != null) {
            holder.textPrazoFinal.setText(dateFormatter.format(prazoFinal));
        } else {
            holder.textPrazoFinal.setText("");
        }

        // Definir o texto da prioridade
        // Assumindo que getTagEnum() retorna a string da prioridade (e.g., "BAIXA", "MEDIA", "ALTA")
        if (tarefa.getTagEnum() != null && !tarefa.getTagEnum().toString().isEmpty()) {
            holder.textPrioridadeTarefa.setText(tarefa.getTagEnum().toString());
        } else {
            holder.textPrioridadeTarefa.setText("N/A"); // Ou um valor padrão se a prioridade for nula/vazia
        }


        // Configura o listener para o botão de exclusão
        if (holder.btnExcluir != null) { // Adicionado verificação de nulo para btnExcluir
            holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Chama o método da Activity para excluir a tarefa
                    if (context instanceof ListaTarefasActivity) {
                        ((ListaTarefasActivity) context).excluirTarefa(tarefa.getId(), holder.getAdapterPosition()); // Usa getAdapterPosition()
                    }
                }
            });
        } else {
            Log.e("TarefaAdapter", "Botão de exclusão (btnExcluir) é nulo no ViewHolder para a posição " + position + ". Verifique o layout item_tarefa.xml.");
        }
    }

    @Override
    public int getItemCount() {
        return listaDeTarefas != null ? listaDeTarefas.size() : 0; // Retorna 0 se a lista for nula
    }

    // Classe interna que representa um item da lista (ViewHolder)
    public static class TarefaViewHolder extends RecyclerView.ViewHolder {
        TextView textTituloTarefa;
        TextView textDescricaoTarefa;
        TextView textStatusTarefa;
        TextView textPrazoFinal;
        TextView textPrioridadeTarefa; // Adicionado TextView para prioridade
        View viewPrioridade; // Mantenha se ainda for usar o indicador de cor
        ImageView btnExcluir; // Adiciona o botão de exclusão

        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloTarefa = itemView.findViewById(R.id.text_titulo_tarefa);
            textDescricaoTarefa = itemView.findViewById(R.id.text_descricao_tarefa);
            textStatusTarefa = itemView.findViewById(R.id.text_status_tarefa);
            textPrazoFinal = itemView.findViewById(R.id.text_prazo_final);
            textPrioridadeTarefa = itemView.findViewById(R.id.text_prioridade_tarefa); // Inicializa o TextView da prioridade
            btnExcluir = itemView.findViewById(R.id.btn_excluir_tarefa); // Inicializa o botão de exclusão (VERIFIQUE ESTE ID NO XML)
        }
    }
}
