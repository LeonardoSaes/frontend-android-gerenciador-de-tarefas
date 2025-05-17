package com.leonardosaes.gerenciador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Classe que estende RecyclerView.Adapter para exibir a lista de tarefas
public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {

    private List<Task> listaDeTarefas; // Lista de tarefas a serem exibidas
    private DateTimeFormatter dateFormatter; // Para formatar a data
    private Context context;

    // Construtor do Adapter: recebe a lista de tarefas e o contexto
    public TarefaAdapter(List<Task> listaDeTarefas, Context context) {
        this.listaDeTarefas = listaDeTarefas;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Define o formato da data
        this.context = context;
    }

    // Metodo chamado para criar um novo ViewHolder (item da lista)
    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item da tarefa (item_tarefa.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarefa, parent, false);
        return new TarefaViewHolder(view); // Retorna um novo ViewHolder com a View inflada
    }

    // Metodo chamado para vincular os dados da tarefa a um ViewHolder existente
    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        // Obtém a tarefa da posição atual na lista
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

        // Define a cor da prioridade (exemplo)
        // holder.viewPrioridade.setBackgroundColor(getColorForPrioridade(tarefa.getPrioridade()));

        // Configura o listener para o botão de exclusão
        holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o método da Activity para excluir a tarefa
                if (context instanceof ListaTarefasActivity) {
                    ((ListaTarefasActivity) context).excluirTarefa(tarefa.getId(), holder.getAdapterPosition()); // Usa getAdapterPosition()
                }
            }
        });
    }

    // Metodo chamado para retornar o número de itens na lista de tarefas
    @Override
    public int getItemCount() {
        return listaDeTarefas.size(); // Retorna o tamanho da lista
    }

    // Classe interna que representa um item da lista (ViewHolder)
    public static class TarefaViewHolder extends RecyclerView.ViewHolder {
        TextView textTituloTarefa;
        TextView textDescricaoTarefa;
        TextView textStatusTarefa;
        TextView textPrazoFinal;
        View viewPrioridade;
        ImageView btnExcluir; // Adiciona o botão de exclusão

        // Construtor do ViewHolder: recebe a View do item da lista
        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloTarefa = itemView.findViewById(R.id.text_titulo_tarefa);
            textDescricaoTarefa = itemView.findViewById(R.id.text_descricao_tarefa);
            textStatusTarefa = itemView.findViewById(R.id.text_status_tarefa);
            textPrazoFinal = itemView.findViewById(R.id.text_prazo_final);
            viewPrioridade = itemView.findViewById(R.id.view_prioridade);
            btnExcluir = itemView.findViewById(R.id.btn_excluir_tarefa); // Inicializa o botão de exclusão
        }
    }
}
