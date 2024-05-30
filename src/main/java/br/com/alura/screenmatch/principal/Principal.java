package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.services.ConsumoAPI;
import br.com.alura.screenmatch.services.ConverteDados;
import com.darkprograms.speech.translator.GoogleTranslate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private SerieRepository repositorio;
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {

            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar series buscadas
                    4 - Buscar série por titulo
                    5 - Buscar série por ator
                    6 - Top 5 séries
                    7 - Buscar por categoria
                    8 - Buscar Por numero de temporadas e avaliacao
                    9 - Buscar episodios por trecho
                    10 - Top 5 episodios de uma série
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarPorNumeroDeTemporadasEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTop5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        //dadosSeries.add(dados);
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma Série pelo nome");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else
            System.out.println("Série não encontrada!");

    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma Série pelo nome");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);


        if (serieBusca.isPresent()) {
            System.out.println("Dados da Serie:" + serieBusca.get());

        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Escreva o nome do ator para busca:");
        var nomeAtor = leitura.nextLine();
        System.out.println("avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        seriesEncontradas.forEach(s ->
                System.out.println("Titulo: " + s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println("Titulo: " + s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Escreva a Categoria:");
        var nomeGenero = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        System.out.println(categoria);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoia: " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarPorNumeroDeTemporadasEAvaliacao() {
        System.out.println("Até quantas temporadas?");
        var temporadas = leitura.nextInt();
        leitura.nextLine();

        System.out.println("avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

//        List<Serie> seriesEncontradas = repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(temporadas, avaliacao);
        List<Serie> seriesEncontradas = repositorio.seriesPorTemporadaEAvaliacao(temporadas, avaliacao);
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() +
                        ", Avaliação = " + s.getAvaliacao() +
                        ", Total de Temporadas = " + s.getTotalTemporadas()
                )
        );
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o Trecho:");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s, Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void buscarTop5EpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s, Temporada %s - Episódio %s - %s - Avaliacao %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(),
                            e.getAvaliacao()));

        }


    }
}
