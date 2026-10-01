package com.darkfantasy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class DarkFantasyGame extends ApplicationAdapter {
    private static final float W=1280,H=720;
    private OrthographicCamera camera;
    private ShapeRenderer shape;
    private SpriteBatch batch;
    private BitmapFont font,small;

    private final Vector2 player=new Vector2(W/2,H/2);
    private final Array<Enemy> enemies=new Array<>();
    private final Array<Orb> orbs=new Array<>();
    private final Array<Bolt> bolts=new Array<>();
    private final Array<Particle> particles=new Array<>();

    private float hp,maxHp,time,spawn,attack,hit;
    private int level,xp,nextXp,kills,power;
    private float speed,cooldown,magnet;
    private boolean paused,gameOver,levelUp;

    @Override public void create() {
        camera=new OrthographicCamera(W,H);
        camera.position.set(W/2,H/2,0); camera.update();
        shape=new ShapeRenderer(); batch=new SpriteBatch();
        font=new BitmapFont(); small=new BitmapFont();
        font.getData().setScale(1.45f);
        reset();
    }

    private void reset() {
        player.set(W/2,H/2); enemies.clear(); orbs.clear(); bolts.clear(); particles.clear();
        hp=maxHp=100; time=spawn=attack=hit=0; level=1; xp=0; nextXp=8; kills=0; power=1;
        speed=230; cooldown=.55f; magnet=80; paused=gameOver=levelUp=false;
        for(int i=0;i<8;i++) spawnEnemy();
    }

    @Override public void render() {
        float dt=Math.min(Gdx.graphics.getDeltaTime(),.05f);
        handleGlobalInput();
        if(!paused&&!gameOver&&!levelUp) update(dt);
        draw();
    }

    private void handleGlobalInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)&&!gameOver&&!levelUp) paused=!paused;
        if(gameOver&&(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)||Gdx.input.justTouched())) reset();
        if(levelUp) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)||Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) upgrade(0);
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)||Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) upgrade(1);
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)||Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) upgrade(2);
        }
    }

    private void update(float dt) {
        time+=dt; spawn-=dt; attack-=dt; hit-=dt;
        move(dt);
        if(spawn<=0) { int n=1+(int)(time/60); for(int i=0;i<Math.min(3,n);i++) spawnEnemy(); spawn=Math.max(.2f,.9f-time*.006f); }
        if(attack<=0) { fire(); attack=cooldown; }
        enemies(dt); bolts(dt); orbs(dt); particles(dt);
    }

    private void move(float dt) {
        float x=0,y=0;
        if(Gdx.input.isKeyPressed(Input.Keys.A)||Gdx.input.isKeyPressed(Input.Keys.LEFT))x--;
        if(Gdx.input.isKeyPressed(Input.Keys.D)||Gdx.input.isKeyPressed(Input.Keys.RIGHT))x++;
        if(Gdx.input.isKeyPressed(Input.Keys.S)||Gdx.input.isKeyPressed(Input.Keys.DOWN))y--;
        if(Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.UP))y++;
        if(Gdx.input.isTouched()) {
            float tx=Gdx.input.getX()/(float)Gdx.graphics.getWidth()*W;
            float ty=H-Gdx.input.getY()/(float)Gdx.graphics.getHeight()*H;
            float dx=tx-player.x,dy=ty-player.y;
            if(dx*dx+dy*dy>900){x=MathUtils.clamp(dx/150,-1,1);y=MathUtils.clamp(dy/150,-1,1);}
        }
        float len=(float)Math.sqrt(x*x+y*y);
        if(len>.01){x/=len;y/=len;player.x=MathUtils.clamp(player.x+x*speed*dt,25,W-25);player.y=MathUtils.clamp(player.y+y*speed*dt,25,H-25);}
    }

    private void spawnEnemy() {
        if(enemies.size>85)return;
        float a=MathUtils.random(MathUtils.PI2),d=MathUtils.random(450,650);
        Enemy e=new Enemy();
        e.x=MathUtils.clamp(player.x+MathUtils.cos(a)*d,15,W-15);
        e.y=MathUtils.clamp(player.y+MathUtils.sin(a)*d,15,H-15);
        e.r=MathUtils.random(11,17); e.hp=e.max=2+level*.5f+MathUtils.random(0,2);
        e.speed=40+time*.3f+MathUtils.random(0,28); e.damage=7+level*.5f;
        enemies.add(e);
    }

    private void enemies(float dt) {
        for(int i=enemies.size-1;i>=0;i--) {
            Enemy e=enemies.get(i),d=new Enemy();
            float dx=player.x-e.x,dy=player.y-e.y,len=(float)Math.sqrt(dx*dx+dy*dy);
            if(len>1){e.x+=dx/len*e.speed*dt;e.y+=dy/len*e.speed*dt;}
            float r=e.r+16;
            if(player.dst2(e.x,e.y)<r*r&&hit<=0) {
                hp-=e.damage;hit=.35f;burst(player.x,player.y,8,Color.valueOf("ff5577"));
                if(hp<=0){hp=0;gameOver=true;}
            }
        }
    }

    private void fire() {
        Enemy target=null;float best=Float.MAX_VALUE;
        for(Enemy e:enemies){float d=player.dst2(e.x,e.y);if(d<best){best=d;target=e;}}
        if(target==null)return;
        Bolt b=new Bolt();b.x=player.x;b.y=player.y;
        Vector2 v=new Vector2(target.x-player.x,target.y-player.y).nor();
        b.vx=v.x*620;b.vy=v.y*620;b.life=1.5f;b.damage=power*(2+level*.25f);bolts.add(b);
    }

    private void bolts(float dt) {
        for(int i=bolts.size-1;i>=0;i--){Bolt b=bolts.get(i);b.x+=b.vx*dt;b.y+=b.vy*dt;b.life-=dt;boolean remove=b.life<=0;
            for(int j=enemies.size-1;j>=0&&!remove;j--){Enemy e=enemies.get(j);float r=e.r+6;
                if(Vector2.dst2(b.x,b.y,e.x,e.y)<r*r){e.hp-=b.damage;burst(b.x,b.y,3,Color.valueOf("c99cff"));remove=true;
                    if(e.hp<=0){kills++;orbs.add(new Orb(e.x,e.y));burst(e.x,e.y,10,Color.valueOf("8d4dff"));enemies.removeIndex(j);}
                }
            } if(remove)bolts.removeIndex(i);
        }
    }

    private void orbs(float dt) {
        for(int i=orbs.size-1;i>=0;i--){Orb o=orbs.get(i);float d2=player.dst2(o.x,o.y);
            if(d2<magnet*magnet){float dx=player.x-o.x,dy=player.y-o.y,d=(float)Math.sqrt(d2);if(d>1){o.x+=dx/d*260*dt;o.y+=dy/d*260*dt;}}
            if(player.dst2(o.x,o.y)<400){xp++;orbs.removeIndex(i);burst(player.x,player.y,2,Color.valueOf("62f0a0"));
                if(xp>=nextXp){xp-=nextXp;nextXp=8+level*5;level++;levelUp=true;}
            }
        }
    }

    private void particles(float dt) {
        for(int i=particles.size-1;i>=0;i--){Particle p=particles.get(i);p.x+=p.vx*dt;p.y+=p.vy*dt;p.life-=dt;if(p.life<=0)particles.removeIndex(i);}
    }

    private void burst(float x,float y,int n,Color c) {
        for(int i=0;i<n;i++){Particle p=new Particle();float a=MathUtils.random(MathUtils.PI2),s=MathUtils.random(30,130);
            p.x=x;p.y=y;p.vx=MathUtils.cos(a)*s;p.vy=MathUtils.sin(a)*s;p.life=p.max=MathUtils.random(.2f,.55f);p.c=new Color(c);particles.add(p);}
    }

    private void draw() {
        Gdx.gl.glClearColor(.025f,.018f,.045f,1);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();shape.setProjectionMatrix(camera.combined);batch.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.valueOf("0b0820"));shape.rect(0,0,W,H);
        shape.setColor(Color.valueOf("17102e"));
        for(int x=0;x<W;x+=64)shape.rect(x,0,1,H);for(int y=0;y<H;y+=64)shape.rect(0,y,W,1);
        for(Orb o:orbs){shape.setColor(Color.valueOf("62f0a0"));shape.circle(o.x,o.y,6);}
        for(Enemy e:enemies){shape.setColor(Color.valueOf("35153b"));shape.circle(e.x,e.y,e.r+3);shape.setColor(Color.valueOf("a83e62"));shape.circle(e.x,e.y,e.r);}
        for(Bolt b:bolts){shape.setColor(Color.valueOf("d4a0ff"));shape.circle(b.x,b.y,5);}
        for(Particle p:particles){float a=MathUtils.clamp(p.life/p.max,0,1);shape.setColor(p.c.r,p.c.g,p.c.b,a);shape.circle(p.x,p.y,2+a*2);}
        shape.setColor(hit>0?Color.WHITE:Color.valueOf("6b4eff"));shape.circle(player.x,player.y,20);
        shape.setColor(Color.valueOf("ddd4ff"));shape.circle(player.x,player.y,13);
        shape.setColor(Color.valueOf("21154b"));shape.circle(player.x,player.y,8);shape.end();
        drawHud();
        if(levelUp)cards();else if(gameOver)gameOver();else if(paused)pause();
    }

    private void drawHud() {
        batch.begin();font.setColor(Color.WHITE);font.draw(batch,"DARK FANTASY // SURVIVOR",24,H-24);
        small.setColor(Color.valueOf("b9afd7"));small.draw(batch,"WASD / ARROWS • TOUCH • SPACE PAUSE",24,H-51);
        small.draw(batch,"LV "+level+"   KILLS "+kills+"   TIME "+timeText(),W-285,H-30);
        font.setColor(Color.valueOf("ff6b8b"));font.draw(batch,"HP",24,52);
        font.setColor(Color.valueOf("6b2944"));font.draw(batch,"████████████████",58,52);
        font.setColor(Color.valueOf("ff6b8b"));font.draw(batch,repeat('█',Math.round(16*hp/maxHp)),58,52);
        small.setColor(Color.valueOf("62f0a0"));small.draw(batch,"SOUL XP",24,28);batch.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);shape.setColor(Color.valueOf("241e3a"));shape.rect(82,20,220,10);
        shape.setColor(Color.valueOf("62f0a0"));shape.rect(82,20,220*xp/(float)nextXp,10);shape.end();
    }

    private void cards() {
        overlay();batch.begin();font.setColor(Color.WHITE);center("ASCENSION",H/2+135);
        small.setColor(Color.valueOf("b9afd7"));centerSmall("Choose an upgrade: 1 / 2 / 3",H/2+105);batch.end();
        String[] n={"BLOOD EDGE","HOLLOW STEP","SOUL MAGNET"},d={"More damage + faster attacks","Move faster + recover","Larger XP pickup radius"};
        for(int i=0;i<3;i++){float x=150+i*330;shape.begin(ShapeRenderer.ShapeType.Filled);shape.setColor(Color.valueOf("1d1536"));shape.rect(x,250,280,190);shape.setColor(Color.valueOf("6b4eff"));shape.rect(x,250,280,3);shape.end();
            batch.begin();font.setColor(Color.WHITE);font.draw(batch,(i+1)+". "+n[i],x+20,390);small.setColor(Color.valueOf("d0c6e8"));small.draw(batch,d[i],x+20,350);small.draw(batch,"Press "+(i+1),x+20,280);batch.end();}
    }

    private void gameOver(){overlay();batch.begin();font.setColor(Color.valueOf("ff6b8b"));center("THE ABYSS CLAIMS YOU",H/2+45);small.setColor(Color.WHITE);centerSmall("Level "+level+" • "+kills+" kills • "+timeText(),H/2);centerSmall("ENTER or tap to rise again",H/2-45);batch.end();}
    private void pause(){overlay();batch.begin();font.setColor(Color.WHITE);center("PAUSED",H/2+10);small.setColor(Color.valueOf("b9afd7"));centerSmall("SPACE to resume",H/2-30);batch.end();}
    private void overlay(){shape.begin(ShapeRenderer.ShapeType.Filled);shape.setColor(0,0,0,.62f);shape.rect(0,0,W,H);shape.end();}
    private void center(String s,float y){GlyphLayout l=new GlyphLayout(font,s);font.draw(batch,s,(W-l.width)/2,y);}
    private void centerSmall(String s,float y){GlyphLayout l=new GlyphLayout(small,s);small.draw(batch,s,(W-l.width)/2,y);}
    private static String repeat(char c,int n){StringBuilder b=new StringBuilder();for(int i=0;i<n;i++)b.append(c);return b.toString();}
    private String timeText(){int t=(int)time;return String.format("%02d:%02d",t/60,t%60);}

    private void upgrade(int i){
        if(i==0){power++;cooldown=Math.max(.22f,cooldown-.05f);}
        else if(i==1){speed+=40;maxHp+=10;hp=Math.min(maxHp,hp+15);}
        else magnet+=50;
        levelUp=false;
    }

    @Override public void dispose(){shape.dispose();batch.dispose();font.dispose();small.dispose();}

    private static class Enemy{float x,y,r,hp,max,speed,damage;}
    private static class Orb{float x,y;Orb(float x,float y){this.x=x;this.y=y;}}
    private static class Bolt{float x,y,vx,vy,life,damage;}
    private static class Particle{float x,y,vx,vy,life,max;Color c;}
}
