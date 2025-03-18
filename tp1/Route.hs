
module Route ( Route, newR, inOrderR)
  where
    
import Data.List (elemIndex)

data Route = Rou [ String ] deriving (Eq, Show)

newR :: [ String ] -> Route                    -- construye una ruta segun una lista de ciudades
newR ciudades | null ciudades = Rou []
              | otherwise = Rou ciudades

inOrderR :: Route -> String -> String -> Bool  -- indica si la primer ciudad consultada esta antes que la segunda ciudad en la ruta
inOrderR (Rou ciudades) ciudad1 ciudad2 | elemIndex ciudad1 ciudades == Nothing || elemIndex ciudad2 ciudades == Nothing = False
                                          | elemIndex ciudad1 ciudades <=  elemIndex ciudad2 ciudades = True
                                          | otherwise = False

inRouteR :: Route -> String -> Bool -- indica si la ciudad consultada está en la ruta
inRouteR (Rou ciudades) str = elem str ciudades