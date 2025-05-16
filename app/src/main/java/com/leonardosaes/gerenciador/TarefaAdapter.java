package com.leonardosaes.gerenciador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    // Construtor do Adapter: recebe a lista de tarefas
    public TarefaAdapter(List<Task> listaDeTarefas) {
        this.listaDeTarefas = listaDeTarefas;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Define o formato da data
    }

    // Método chamado para criar um novo ViewHolder (item da lista)
    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item da tarefa (item_tarefa.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarefa, parent, false);
        return new TarefaViewHolder(view); // Retorna um novo ViewHolder com a View inflada
    }

    // Método chamado para vincular os dados da tarefa a um ViewHolder existente
    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        // Obtém a tarefa da posição atual na lista
        Task tarefa = listaDeTarefas.get(position);

        // Define os valores dos TextViews do ViewHolder com os dados da tarefa
        holder.textTituloTarefa.setText(tarefa.getTitulo());
        holder.textDescricaoTarefa.setText(tarefa.getDescricao());
        holder.textStatusTarefa.setText(tarefa.getStatusTarefa()); // Supondo que exista um getStatus()

        LocalDate prazoFinal = tarefa.getPrazoFinal();
        if (prazoFinal != null) {
            holder.textPrazoFinal.setText(dateFormatter.format(prazoFinal)); // Formata LocalDate para String
        } else {
            holder.textPrazoFinal.setText(""); // Ou alguma outra representação de data nula
        }

        //TODO: Implementar logica para a prioridade
        // Aqui você precisara adicionar lógica para definir a cor de `holder.viewPrioridade`
        // com base na prioridade da tarefa.  Supondo que sua classe Task tenha um getPrioridade
        /*
        int prioridade = tarefa.getPrioridade();
        switch (prioridade) {
            case 1: // Prioridade Alta
                holder.viewPrioridade.setBackgroundColor(Color.RED);
                break;
            case 2: // Prioridade Média
                holder.viewPrioridade.setBackgroundColor(Color.YELLOW);
                break;
            case 3: // Prioridade Baixa
                holder.viewPrioridade.setBackgroundColor(Color.GREEN);
                break;
            default:
                holder.viewPrioridade.setBackgroundColor(Color.GRAY);
                break;
        }
        */
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

        // Construtor do ViewHolder: recebe a View do item da lista
        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa os TextViews com os IDs definidos no layout item_tarefa.xml
            textTituloTarefa = itemView.findViewById(R.id.text_titulo_tarefa);
            textDescricaoTarefa = itemView.findViewById(R.id.text_descricao_tarefa);
            textStatusTarefa = itemView.findViewById(R.id.text_status_tarefa);
            textPrazoFinal = itemView.findViewById(R.id.text_prazo_final);
            viewPrioridade = itemView.findViewById(R.id.view_prioridade);
        }
    }
}
