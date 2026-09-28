-- ==============================================================================
-- SCHEMA DE BASE DE DATOS PARA APP REGISTROS (SUPABASE / POSTGRESQL)
-- ==============================================================================
-- Este archivo contiene las tablas, índices, políticas de seguridad (RLS)
-- y vistas para la aplicación móvil y web.
--
-- Para ejecutarlo:
-- 1. Ve a tu proyecto en Supabase (https://app.supabase.com)
-- 2. Entra en "SQL Editor" -> "New query"
-- 3. Pega este contenido y presiona "Run"
-- ==============================================================================

-- 1. EXTENSIÓN PARA GENERAR UUIDs (incluida por defecto en Postgres)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. TABLA PRINCIPAL DE REGISTROS
CREATE TABLE IF NOT EXISTS public.registros (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(100) DEFAULT 'General',
    monto NUMERIC(12, 2) DEFAULT 0.00,
    fecha TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW()),
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW()),
    updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc', NOW())
);

-- 3. ÍNDICES PARA BÚSQUEDAS RÁPIDAS Y ALTO RENDIMIENTO
CREATE INDEX IF NOT EXISTS idx_registros_user_id ON public.registros(user_id);
CREATE INDEX IF NOT EXISTS idx_registros_fecha ON public.registros(fecha DESC);
CREATE INDEX IF NOT EXISTS idx_registros_categoria ON public.registros(categoria);

-- 4. TRIGGER PARA ACTUALIZAR AUTOMÁTICAMENTE 'updated_at'
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = TIMEZONE('utc', NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_registros_updated_at ON public.registros;
CREATE TRIGGER trigger_registros_updated_at
    BEFORE UPDATE ON public.registros
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

-- ==============================================================================
-- 5. SEGURIDAD A NIVEL DE FILA (ROW LEVEL SECURITY - RLS)
-- ==============================================================================
-- Esto garantiza que CADA USUARIO solo pueda ver, crear, modificar o borrar
-- SUS PROPIOS REGISTROS. Ningún usuario podrá espiar datos de otros.
-- ==============================================================================

ALTER TABLE public.registros ENABLE ROW LEVEL SECURITY;

-- Política de Lectura (SELECT)
CREATE POLICY "Permitir a los usuarios consultar sus propios registros"
    ON public.registros
    FOR SELECT
    USING (auth.uid() = user_id);

-- Política de Inserción (INSERT)
CREATE POLICY "Permitir a los usuarios insertar sus propios registros"
    ON public.registros
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Política de Actualización (UPDATE)
CREATE POLICY "Permitir a los usuarios actualizar sus propios registros"
    ON public.registros
    FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

-- Política de Eliminación (DELETE)
CREATE POLICY "Permitir a los usuarios borrar sus propios registros"
    ON public.registros
    FOR DELETE
    USING (auth.uid() = user_id);

-- ==============================================================================
-- 6. CONSULTAS DE EJEMPLO PARA EXPORTAR DATOS FÁCILMENTE (PORTABILIDAD)
-- ==============================================================================
-- Si en el futuro quieres extraer todos tus datos a JSON o CSV sin tocar código:
--
-- Exportar a JSON:
-- SELECT json_agg(r) FROM public.registros r WHERE user_id = auth.uid();
--
-- O directamente en el panel de Supabase:
-- Tabla 'registros' -> Botón 'Export' -> 'Download as CSV' o 'Download as JSON'.
-- ==============================================================================
