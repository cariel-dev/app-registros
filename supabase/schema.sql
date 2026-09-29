-- ==============================================================================
-- SCHEMA POSTGRESQL (SUPABASE) - APP DE MEDIDAS CORPORALES Y PLICOMETRÍA
-- ==============================================================================
-- Diseñado siguiendo las mejores prácticas de Supabase:
-- 1. UUIDs generados en cliente/servidor para sincronización offline sin colisiones.
-- 2. Tipos de datos precisos: NUMERIC(5,2) para peso y cm, NUMERIC(4,1) para pliegues en mm.
-- 3. Índices compuestos para consultas rápidas por usuario y fecha.
-- 4. Row Level Security (RLS) estricto: cada usuario solo ve sus propias medidas.
-- ==============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- TABLA PRINCIPAL: mediciones_corporales
CREATE TABLE IF NOT EXISTS public.mediciones_corporales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    fecha_hora TIMESTAMPTZ NOT NULL DEFAULT TIMEZONE('utc', NOW()),
    
    -- Peso y notas generales
    peso_kg NUMERIC(5,2) NOT NULL,
    notas TEXT,

    -- Circunferencias corporales (en centímetros)
    cuello NUMERIC(5,2),
    hombros NUMERIC(5,2),
    pecho NUMERIC(5,2),
    cintura NUMERIC(5,2),
    cadera NUMERIC(5,2),
    biceps_der NUMERIC(5,2),
    biceps_izq NUMERIC(5,2),
    antebrazo_der NUMERIC(5,2),
    antebrazo_izq NUMERIC(5,2),
    muslo_der NUMERIC(5,2),
    muslo_izq NUMERIC(5,2),
    pantorrilla_der NUMERIC(5,2),
    pantorrilla_izq NUMERIC(5,2),

    -- Plicometría / Pliegues cutáneos (en milímetros)
    pliegue_triceps NUMERIC(4,1),
    pliegue_subescapular NUMERIC(4,1),
    pliegue_suprailiaco NUMERIC(4,1),
    pliegue_abdominal NUMERIC(4,1),
    pliegue_muslo NUMERIC(4,1),
    pliegue_pectoral NUMERIC(4,1),
    pliegue_axilar NUMERIC(4,1),

    -- Estimación calculada de porcentaje de grasa corporal (%)
    porcentaje_grasa NUMERIC(4,2),

    -- Auditoría
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW()),
    updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW())
);

-- ÍNDICES DE RENDIMIENTO (Consulta frecuente: mediciones de un usuario ordenadas por fecha)
CREATE INDEX IF NOT EXISTS idx_mediciones_user_fecha 
    ON public.mediciones_corporales(user_id, fecha_hora DESC);

-- TRIGGER PARA ACTUALIZAR AUTOMÁTICAMENTE 'updated_at'
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = TIMEZONE('utc', NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_mediciones_updated_at ON public.mediciones_corporales;
CREATE TRIGGER trigger_mediciones_updated_at
    BEFORE UPDATE ON public.mediciones_corporales
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

-- ==============================================================================
-- SEGURIDAD A NIVEL DE FILA (ROW LEVEL SECURITY - RLS)
-- ==============================================================================
ALTER TABLE public.mediciones_corporales ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Usuarios pueden leer únicamente sus propias mediciones"
    ON public.mediciones_corporales
    FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Usuarios pueden insertar sus propias mediciones"
    ON public.mediciones_corporales
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Usuarios pueden actualizar sus propias mediciones"
    ON public.mediciones_corporales
    FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Usuarios pueden eliminar sus propias mediciones"
    ON public.mediciones_corporales
    FOR DELETE
    USING (auth.uid() = user_id);

-- ==============================================================================
-- TABLA DE PERFILES PARA LOGIN SENCILLO POR NOMBRE DE USUARIO
-- ==============================================================================
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW())
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Lectura pública de nombres de usuario para validar disponibilidad"
    ON public.profiles
    FOR SELECT
    USING (true);

CREATE POLICY "Usuarios pueden insertar su propio perfil"
    ON public.profiles
    FOR INSERT
    WITH CHECK (auth.uid() = id);
