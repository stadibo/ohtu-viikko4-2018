/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu;

import ohtu.verkkokauppa.Kauppa;
import ohtu.verkkokauppa.Pankki;
import ohtu.verkkokauppa.Tuote;
import ohtu.verkkokauppa.Varasto;
import ohtu.verkkokauppa.Viitegeneraattori;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peje
 */
public class KauppaTest {
    
    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;

    @Before
    public void setUp() {
        // luodaan ensin mock-oliot
        pankki = mock(Pankki.class);
        viite = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);
    }
    
    @Test
    public void ostoksenPaatyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaParametreilla() {
        when(viite.uusi()).thenReturn(42);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(eq("harri"), eq(42), eq("12345"), anyString(), eq(5));
    }
    
    @Test
    public void kunOnLisattyKaksiEriTuotettaPankinMetodiaTilisiirtoKutsutaanOikein() {
        when(viite.uusi()).thenReturn(42);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(20);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "leipä", 3));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(eq("harri"), eq(42), eq("12345"), anyString(), eq(8));
    }
    
    @Test
    public void kunOnLisattyKaksiSamaaTuotettaPankinMetodiaTilisiirtoKutsutaanOikein() {
        when(viite.uusi()).thenReturn(42);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(eq("harri"), eq(42), eq("12345"), anyString(), eq(10));
    }
    
    @Test
    public void kunTuoteOnLoppuVarastostaJaLisattuOstokseenPankinMetodiaTilisiirtoKutsutaanOikein() {
        when(viite.uusi()).thenReturn(42);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "leipä", 3));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(eq("harri"), anyInt(), eq("12345"), anyString(), eq(5));
    }
    
    @Test
    public void aloitaAsiointiMetodinKutsuminenNollaaEdellisenOstoskorinTiedot() {
        when(viite.uusi()).thenReturn(42).thenReturn(43);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(15);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "leipä", 3));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(eq("harri"), eq(42), eq("12345"), anyString(), eq(8));
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("roger", "54321");
        
        verify(pankki).tilisiirto(eq("roger"), eq(43), eq("54321"), anyString(), eq(5));
    }
    
    @Test
    public void pyydetaanUusiViiteJokaiseenMaksuun() {
        when(viite.uusi())
                .thenReturn(42)
                .thenReturn(43)
                .thenReturn(44);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(anyString(), eq(42), anyString(), anyString(), anyInt());
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("roger", "54321");
        
        verify(pankki).tilisiirto(anyString(), eq(43), anyString(), anyString(), anyInt());
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("tim", "12321");
        
        verify(pankki).tilisiirto(anyString(), eq(44), anyString(), anyString(), anyInt());
    }
    
    @Test
    public void kunTuoteOnPoistettuOstoskoristaPankinMetodiaTilisiirtoKutsutaanOikein() {
        when(viite.uusi()).thenReturn(42);
        
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        Kauppa k = new Kauppa(varasto, pankki, viite);
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(1);
        k.poistaKorista(1);
        k.tilimaksu("harri", "12345");
        
        verify(pankki).tilisiirto(eq("harri"), anyInt(), eq("12345"), anyString(), eq(5));
    }
    
}
