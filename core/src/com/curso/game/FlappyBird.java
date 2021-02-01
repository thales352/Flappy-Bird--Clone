package com.curso.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	private Texture  fundo,canoCima,canoBaixo,gameOver;
	private Texture[] passaros;
	private int posicaoInicial, velocidadeQueda, larguraDispositivo,
			alturaDispositivo = 0,alturaDoCano, estadoJogo=0, pontuacao=0;
	private float movimento=0,posicaoMovimentoHorizontal,distanciaEntreTubos,
			deltaTime,VIRTUAL_WIDTH=1080,VIRTUAL_HEIGHT=1920;
	private BitmapFont font,fim;
	private Boolean marcouPonto = false;
	private Circle passaroCirculo;
	private Rectangle canoCimarec, canoBaixorec;
	private OrthographicCamera camera;
	private Viewport viewport;
	
	@Override
	public void create () {
		batch 		= new SpriteBatch();

		passaroCirculo = new Circle();
		canoBaixorec   = new Rectangle();
		canoCimarec    = new Rectangle();


		font 		= new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(6);

		fim 		= new BitmapFont();
		fim.setColor(Color.WHITE);
		fim.getData().setScale(3);

		fundo 		= new Texture("fundo.png");
		passaros 	= new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		canoCima 	= new Texture("cano_topo_maior.png");
		canoBaixo 	= new Texture("cano_baixo_maior.png");
		gameOver	= new Texture("game_over.png");

		larguraDispositivo 	= (int)VIRTUAL_WIDTH;
		alturaDispositivo 	= (int)VIRTUAL_HEIGHT;
		posicaoInicial 		= alturaDispositivo/2;
		posicaoMovimentoHorizontal = larguraDispositivo;
		distanciaEntreTubos = 300;

		camera		= new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
		viewport	= new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);
	}

	@Override
	public void render () {
		deltaTime = Gdx.graphics.getDeltaTime();
		movimento += deltaTime*10;
		if (movimento>2){movimento = 0;}

		if (estadoJogo == 0){
			if (Gdx.input.justTouched()){
				estadoJogo =1;
			}
		}else {
			velocidadeQueda++;
			if (posicaoInicial>0||velocidadeQueda<0){ posicaoInicial-=velocidadeQueda;}
			if(estadoJogo ==1){
				posicaoMovimentoHorizontal-=deltaTime*600;
				if(posicaoMovimentoHorizontal<0-canoCima.getWidth()){
					posicaoMovimentoHorizontal=larguraDispositivo;
					alturaDoCano= new Random().nextInt(400)-200;
					marcouPonto = false;
				}
				if(Gdx.input.justTouched()){velocidadeQueda = -20;}
				if(posicaoMovimentoHorizontal<70-canoBaixo.getWidth()){
					if(!marcouPonto){
						pontuacao++;
						marcouPonto=true;}

				}
			}else {
				if(Gdx.input.justTouched()){
					estadoJogo = 0;
					posicaoMovimentoHorizontal=larguraDispositivo;
					posicaoInicial = alturaDispositivo/2;
					velocidadeQueda = 0;
					pontuacao=0;
				}
			}
		}
		camera.update();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(fundo    , 0, 0,larguraDispositivo,alturaDispositivo);
		batch.draw(canoCima ,posicaoMovimentoHorizontal,alturaDispositivo/2+(distanciaEntreTubos/2)+alturaDoCano,canoCima.getWidth(),alturaDispositivo);
		font.draw(batch,String.valueOf(pontuacao),larguraDispositivo/2,alturaDispositivo-50);
		batch.draw(canoBaixo,posicaoMovimentoHorizontal,alturaDispositivo/2-alturaDispositivo-distanciaEntreTubos/2+alturaDoCano,canoBaixo.getWidth(),alturaDispositivo);
		batch.draw(passaros[(int)movimento],70, posicaoInicial);
		if (estadoJogo==2){
			batch.draw(gameOver,larguraDispositivo/2-gameOver.getWidth()/2,alturaDispositivo/2-gameOver.getHeight()/2);
			fim.draw(batch,"Clique para reiniciar!",larguraDispositivo/2-200,alturaDispositivo/2-gameOver.getHeight()/2);
		}
		batch.end();

		canoBaixorec.set(posicaoMovimentoHorizontal,alturaDispositivo/2+(distanciaEntreTubos/2)+alturaDoCano,canoCima.getWidth(),alturaDispositivo);
		canoCimarec.set(posicaoMovimentoHorizontal,alturaDispositivo/2-alturaDispositivo-distanciaEntreTubos/2+alturaDoCano,canoBaixo.getWidth(),alturaDispositivo);
		passaroCirculo.set(70+passaros[0].getWidth()/2,posicaoInicial+passaros[0].getHeight()/2,passaros[0].getWidth()/2);

		if(Intersector.overlaps(passaroCirculo,canoBaixorec)|| Intersector.overlaps(passaroCirculo,canoCimarec)||posicaoInicial<=0||posicaoInicial>=alturaDispositivo){
			estadoJogo = 2;

		}

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}

	@Override
	public void dispose () {
		batch.dispose();
		passaros[0].dispose();
		passaros[1].dispose();
		passaros[2].dispose();
		canoBaixo.dispose();
		canoCima.dispose();
	}
}
