package br.com.alura.screenmatch;

import br.com.alura.screenmatch.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Principal principal = new Principal();
		principal.exibeMenu();

//		var consumoAPI = new ConsumoAPI();
//		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
//		System.out.println(json);
//
//
//
//		ConverteDados conversor = new ConverteDados();
//		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//		System.out.println(dados);
//
//		json = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=6585022c");
//
//		DadosEpisodio dadosEpisodio = conversor.obterDados(json,DadosEpisodio.class);
//		System.out.println(dadosEpisodio);
//		List<DadosTemporada> temporadas = new ArrayList<>();
//
//		for (int i = 1; i<=dados.totalTemporadas(); i++){
//			json = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&season="+ i +"&apikey=6585022c");
//			DadosTemporada dadosTemporada = conversor.obterDados(json,DadosTemporada.class);
//			temporadas.add(dadosTemporada);
//
//		}
//		System.out.println("Lista de ep");
//		temporadas.forEach(System.out::println);
//
//
	}


}
